package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.ChallengeTemplate;
import uno.cod.platform.server.core.domain.Endpoint;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateCreateDto;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateShowDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
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

    public UUID save(ChallengeTemplateCreateDto dto) {
        Organization organization = organizationRepository.findOne(dto.getOrganizationId());
        if (organization == null) {
            throw new CodunoIllegalArgumentException("organization.invalid");
        }
        Endpoint endpoint = endpointRepository.findOne(dto.getEndpointId());
        if (endpoint == null) {
            throw new CodunoIllegalArgumentException("endpoint.invalid");
        }
        if (repository.findOneByCanonicalName(dto.getCanonicalName()) != null) {
            throw new CodunoIllegalArgumentException("challenge.template.canonical.name.exists");
        }
        ChallengeTemplate challengeTemplate = new ChallengeTemplate(dto.getCanonicalName(), dto.getName());
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

    public ChallengeTemplateShowDto findByCanonicalName(String canonicalName) {
        return ChallengeTemplateMapper.map(repository.findOneByCanonicalNameWithEndpointAndTasksAndChallenges(canonicalName));
    }

    public List<ChallengeTemplateShowDto> findAll(UUID organizationId) {
        return ChallengeTemplateMapper.map(repository.findAllWithTasks(organizationId));
    }

    public ChallengeTemplateShowDto findByChallengeCanonicalName(String canonicalName) {
        return ChallengeTemplateMapper.map(challengeRepository.findOneByCanonicalNameWithTemplate(canonicalName).getChallengeTemplate());
    }
}
