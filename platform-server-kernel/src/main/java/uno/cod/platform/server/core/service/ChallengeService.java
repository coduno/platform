package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.dto.challenge.UserChallengeShowDto;
import uno.cod.platform.server.core.mapper.ChallengeMapper;
import uno.cod.platform.server.core.repository.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChallengeService {
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final ChallengeRepository repository;
    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;

    @Autowired
    public ChallengeService(ChallengeRepository repository,
                            ChallengeTemplateRepository challengeTemplateRepository,
                            ResultRepository resultRepository,
                            UserRepository userRepository, ParticipationRepository participationRepository) {
        this.repository = repository;
        this.challengeTemplateRepository = challengeTemplateRepository;
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
        this.participationRepository = participationRepository;
    }

    public UUID createFromDto(ChallengeCreateDto dto) {
        ChallengeTemplate template = challengeTemplateRepository.findOne(dto.getTemplateId());
        if (template == null) {
            throw new IllegalArgumentException("challenge.invalid");
        }
        Challenge challenge = new Challenge();
        challenge.setChallengeTemplate(template);
        challenge.setName(dto.getName());
        challenge.setCanonicalName(dto.getCanonicalName());
        if (dto.getStartDate() != null) {
            challenge.setStartDate(dto.getStartDate());
            challenge.setEndDate(dto.getStartDate().plus(template.getDuration()));
        }
        challenge.setInviteOnly(dto.isInviteOnly());
        return repository.save(challenge).getId();
    }

    public ChallengeDto findOneById(UUID challengeId) {
        return ChallengeMapper.map(repository.findOne(challengeId));
    }

    public void register(User user, String canonicalName) {
        Challenge challenge = repository.findOneByCanonicalName(canonicalName);
        if (challenge == null) {
            throw new IllegalArgumentException("challenge.invalid");
        }
        if (participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId()) != null) {
            throw new IllegalArgumentException("participation.invalid");
        }
        user = userRepository.findOneWithChallenges(user.getId());
        Participation participation = new Participation();
        participation.setChallenge(challenge);
        participation.setUser(user);
        participationRepository.save(participation);
        repository.save(challenge);
        userRepository.save(user);
    }

    public List<UserChallengeShowDto> getPublicChallenges(final User user) {
        List<Challenge> challenges = repository.findAllWithOrganizationAndInvitedUsersAndRegisteredUsers();
        return challenges.stream().map(challenge -> {
            UserChallengeShowDto dto = new UserChallengeShowDto();
            dto.setChallenge(new ChallengeDto(challenge));
            UserChallengeShowDto.ChallengeStatus status = null;

            if (challenge.getInvitedUsers() != null && challenge.getInvitedUsers().contains(user)) {
                status = UserChallengeShowDto.ChallengeStatus.INVITED;
            } else if (challenge.getParticipations() != null) {
                for (Participation participation : challenge.getParticipations()) {
                    if (participation.getUser().getId().equals(user.getId())) {
                        status = UserChallengeShowDto.ChallengeStatus.REGISTERED;
                        break;
                    }
                }
            }
            Result result = resultRepository.findOneByUserAndChallenge(user.getId(), dto.getChallenge().getId());
            if (result != null && result.getStarted() != null) {
                if (result.getFinished() != null) {
                    status = UserChallengeShowDto.ChallengeStatus.COMPLETED;
                } else {
                    status = UserChallengeShowDto.ChallengeStatus.IN_PROGRESS;
                }
            }
            dto.setStatus(status);
            if (status != null) {
                return dto;
            }
            if (challenge.isInviteOnly()) {
                return null;
            }
            dto.setStatus(UserChallengeShowDto.ChallengeStatus.OPEN);
            return dto;

        }).filter(p -> p != null).collect(Collectors.toList());
    }

}
