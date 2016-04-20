package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.ChallengeTemplate;
import uno.cod.platform.server.core.domain.Endpoint;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateCreateDto;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateShowDto;
import uno.cod.platform.server.core.mapper.ChallengeTemplateMapper;
import uno.cod.platform.server.core.repository.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChallengeTemplateService {
    private final ChallengeTemplateRepository repository;
    private final ChallengeRepository challengeRepository;
    private final TaskRepository taskRepository;
    private final OrganizationRepository organizationRepository;
    private final EndpointRepository endpointRepository;

    @Autowired
    public ChallengeTemplateService(ChallengeTemplateRepository repository,
                                    ChallengeRepository challengeRepository,
                                    TaskRepository taskRepository,
                                    OrganizationRepository organizationRepository,
                                    EndpointRepository endpointRepository) {
        this.repository = repository;
        this.challengeRepository = challengeRepository;
        this.taskRepository = taskRepository;
        this.organizationRepository = organizationRepository;
        this.endpointRepository = endpointRepository;
    }

    public UUID save(ChallengeTemplateCreateDto dto, UUID organizationId) {
        Organization organization = organizationRepository.findOne(organizationId);
        if (organization == null) {
            throw new IllegalArgumentException("organization.invalid");
        }
        Endpoint endpoint = endpointRepository.findOne(dto.getEndpointId());
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint.invalid");
        }
        ChallengeTemplate challengeTemplate = new ChallengeTemplate();
        challengeTemplate.setName(dto.getName());
        challengeTemplate.setDescription(dto.getDescription());
        challengeTemplate.setInstructions(dto.getInstructions());
        challengeTemplate.setDuration(dto.getDuration());
        for (UUID taskId : dto.getTasks()) {
            challengeTemplate.addTask(taskRepository.getOne(taskId));
        }
        organization.addChallenge(challengeTemplate);
        endpoint.addChallenge(challengeTemplate);

        return repository.save(challengeTemplate).getId();
    }

    public ChallengeTemplateShowDto findById(UUID id) {
        return ChallengeTemplateMapper.map(repository.findOneWithEndpointAndTasksAndChallenges(id));
    }

    public List<ChallengeTemplateShowDto> findAll(UUID organizationId) {
        return ChallengeTemplateMapper.map(repository.findAllWithTasks(organizationId));
    }

    public ChallengeTemplateShowDto findByChallengeId(UUID challengeId) {
        return ChallengeTemplateMapper.map(challengeRepository.findOneWithTemplate(challengeId).getChallengeTemplate());
    }
}
