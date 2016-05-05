package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Endpoint;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.domain.Runner;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.mapper.TaskMapper;
import uno.cod.platform.server.core.repository.EndpointRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.RunnerRepository;
import uno.cod.platform.server.core.repository.TaskRepository;

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

    @Autowired
    public TaskService(TaskRepository repository, EndpointRepository endpointRepository, OrganizationRepository organizationRepository, RunnerRepository runnerRepository) {
        this.repository = repository;
        this.endpointRepository = endpointRepository;
        this.organizationRepository = organizationRepository;
        this.runnerRepository = runnerRepository;
    }

    public UUID save(TaskCreateDto dto) {
        Endpoint endpoint = endpointRepository.findOne(dto.getEndpointId());
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint.invalid");
        }
        Organization organization = organizationRepository.findOne(dto.getOrganizationId());
        if (organization == null) {
            throw new IllegalArgumentException("organization.invalid");
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
            throw new IllegalArgumentException("skills.invalid");
        }
        Task task = new Task();
        task.setName(dto.getName());
        task.setInstructions(dto.getInstructions());
        task.setDescription(dto.getDescription());
        task.setPublic(dto.isPublic());
        task.setDuration(dto.getDuration());
        task.setSkillMap(dto.getSkillMap());
        task.setRunner(runner);
        task.setParams(dto.getParams());
        task.setCanonicalName(dto.getCanonicalName());
        endpoint.addTask(task);
        organization.addTask(task);
        return repository.save(task).getId();
    }

    public TaskShowDto findById(UUID id) {
        return TaskMapper.map(repository.findOneWithLanguagesAndTemplates(id));
    }

    public List<TaskShowDto> findAll(UUID organizationId) {
        return TaskMapper.map(repository.findAllWithEndpoints(organizationId));
    }

    public List<TaskShowDto> findAllForChallengeTemplate(UUID challengeTemplateId) {
        return TaskMapper.map(repository.findAllByChallengeTemplatesId(challengeTemplateId));
    }
}
