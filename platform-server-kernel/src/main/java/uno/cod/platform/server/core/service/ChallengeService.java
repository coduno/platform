package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.Endpoint;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeShowDto;
import uno.cod.platform.server.core.mapper.ChallengeMapper;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.EndpointRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.TaskRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ChallengeService {
    private final ChallengeRepository repository;
    private final TaskRepository taskRepository;
    private final OrganizationRepository organizationRepository;
    private final EndpointRepository endpointRepository;

    @Autowired
    public ChallengeService(ChallengeRepository repository, TaskRepository taskRepository, OrganizationRepository organizationRepository, EndpointRepository endpointRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.organizationRepository = organizationRepository;
        this.endpointRepository = endpointRepository;
    }

    public Long save(ChallengeCreateDto dto) {
        Organization organization = organizationRepository.findOne(dto.getOrganizationId());
        if(organization==null){
            throw new IllegalArgumentException("organization.invalid");
        }
        Endpoint endpoint = endpointRepository.findOne(dto.getEndpointId());
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint.invalid");
        }
        Challenge challenge = new Challenge();
        challenge.setName(dto.getName());
        challenge.setDescription(dto.getDescription());
        challenge.setInstructions(dto.getInstructions());
        challenge.setDuration(dto.getDuration());
        for (Long taskId : dto.getTasks()) {
            challenge.addTask(taskRepository.getOne(taskId));
        }
        organization.addChallenge(challenge);
        endpoint.addChallenge(challenge);

        return repository.save(challenge).getId();
    }

    public ChallengeShowDto findById(Long id) {
        return ChallengeMapper.map(repository.findOneWithEndpointAndTasks(id));
    }

    public List<ChallengeShowDto> findAll(Long organizationId) {
        return ChallengeMapper.map(repository.findAllWithTasks(organizationId));
    }
}
