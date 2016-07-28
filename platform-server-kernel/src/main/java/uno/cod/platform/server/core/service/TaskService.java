package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.exception.CodunoAccessDeniedException;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.mapper.TaskMapper;
import uno.cod.platform.server.core.repository.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskService {
    private final TaskRepository repository;
    private final EndpointRepository endpointRepository;
    private final OrganizationRepository organizationRepository;
    private final RunnerRepository runnerRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;
    private final HttpSession httpSession;

    @Autowired
    public TaskService(TaskRepository repository,
                       EndpointRepository endpointRepository,
                       OrganizationRepository organizationRepository,
                       RunnerRepository runnerRepository,
                       ChallengeRepository challengeRepository,
                       UserRepository userRepository,
                       ResultRepository resultRepository,
                       HttpSession httpSession) {
        this.repository = repository;
        this.endpointRepository = endpointRepository;
        this.organizationRepository = organizationRepository;
        this.runnerRepository = runnerRepository;
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.resultRepository = resultRepository;
        this.httpSession = httpSession;
    }

    public UUID save(TaskCreateDto dto) {
        Endpoint endpoint = endpointRepository.findOne(dto.getEndpointId());
        if (endpoint == null) {
            throw new CodunoIllegalArgumentException("endpoint.invalid");
        }
        Organization organization = organizationRepository.findOne(dto.getOrganizationId());
        if (organization == null) {
            throw new CodunoIllegalArgumentException("organization.invalid");
        }
        Runner runner = null;
        if (dto.getRunnerId() != null) {
            runner = runnerRepository.findOne(dto.getRunnerId());
        }
        double skillSum = 0;
        for (Double skill : dto.getSkillMap().values()) {
            skillSum += skill;
        }
        if (skillSum != 1) {
            throw new CodunoIllegalArgumentException("skills.invalid");
        }
        Task task = new Task(dto.getCanonicalName(), dto.getName());
        task.setInstructions(dto.getInstructions());
        task.setDescription(dto.getDescription());
        task.setPublic(dto.isPublic());
        task.setDuration(dto.getDuration());
        task.setSkillMap(dto.getSkillMap());
        task.setRunner(runner);
        task.setParams(dto.getParams());
        endpoint.addTask(task);
        organization.addTask(task);
        return repository.save(task).getId();
    }

    public TaskShowDto findById(UUID taskId, User u) {
        Task task = repository.findOneWithTemplates(taskId);
        User user = userRepository.findOne(u.getId());
        Object attr = httpSession.getAttribute(ResultService.CURRENT_CHALLENGE);
        if (attr == null) {
            throw new CodunoAccessDeniedException("challenge.invalid");
        }

        Challenge challenge = challengeRepository.findOne((UUID)attr);

        if (canSeeTask(user, challenge, task)) {
            return TaskMapper.map(task);
        }
        throw new CodunoAccessDeniedException("task.denied");
    }

    private boolean canSeeTask(User user, Challenge challenge, Task task) {
        int requestedIndex = challenge.getChallengeTemplate().getTasks().indexOf(task);

        // User can see task if it is the first task (requestedIndex == 0) or the previous task was completed
        // successfully.
        return requestedIndex == 0 || resultRepository.findOneByUserAndChallenge(user.getId(), challenge.getId()).getTaskResults().get(requestedIndex - 1).isSuccessful();
    }

    public List<TaskShowDto> findAllForOrganization(UUID organizationId) {
        return TaskMapper.map(repository.findAllByOrganizationIdWithEndpoints(organizationId));
    }

    public List<TaskShowDto> findAll() {
        return TaskMapper.map(repository.findAllWithAll());
    }

    public List<TaskShowDto> findAllForChallengeTemplate(String canonicalName) {
        return TaskMapper.map(repository.findAllByChallengeTemplatesCanonicalName(canonicalName));
    }
}
