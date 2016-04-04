package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.ChallengeTemplate;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.dto.challenge.UserChallengeShowDto;
import uno.cod.platform.server.core.mapper.ChallengeMapper;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ChallengeTemplateRepository;
import uno.cod.platform.server.core.repository.ResultRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChallengeService {
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final ChallengeRepository repository;
    private final ResultRepository resultRepository;

    @Autowired
    public ChallengeService(ChallengeRepository repository,
                            ChallengeTemplateRepository challengeTemplateRepository, ResultRepository resultRepository) {
        this.repository = repository;
        this.challengeTemplateRepository = challengeTemplateRepository;
        this.resultRepository = resultRepository;
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

    public List<UserChallengeShowDto> getUserChallenges(User user){
        List<UserChallengeShowDto> dtos = new ArrayList<>();
        List<Challenge> challenges = repository.findAllByInvitedUser(user.getId());
        for(Challenge challenge: challenges){
            UserChallengeShowDto dto = new UserChallengeShowDto();
            dto.setChallenge(new ChallengeDto(challenge));
            dtos.add(dto);
        }
        for(UserChallengeShowDto dto: dtos){
            Result result = resultRepository.findOneByUserAndChallenge(user.getId(), dto.getChallenge().getId());
            if(result!=null && result.getStarted()!=null){
                if(result.getFinished()!=null){
                    dto.setStatus(UserChallengeShowDto.ChallengeStatus.COMPLETED);
                }else{
                    dto.setStatus(UserChallengeShowDto.ChallengeStatus.IN_PROGRESS);
                }
            }
        }
        return dtos;
    }

}
