package uno.cod.platform.server.codingcontest.sync.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.codingcontest.sync.dto.CodingcontestDto;
import uno.cod.platform.server.codingcontest.sync.dto.ContestInfoDto;
import uno.cod.platform.server.codingcontest.sync.dto.ContestantDto;
import uno.cod.platform.server.codingcontest.sync.dto.ParticipationDto;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ChallengeTemplateRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CodingcontestSyncService {
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final ResultRepository resultRepository;

    @Autowired
    public CodingcontestSyncService(UserRepository userRepository, ChallengeRepository challengeRepository, ChallengeTemplateRepository challengeTemplateRepository, ResultRepository resultRepository) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.challengeTemplateRepository = challengeTemplateRepository;
        this.resultRepository = resultRepository;
    }

    public User updateUserFromCodingcontest(ParticipationDto dto) {
        User user = userRepository.findOne(UUID.fromString(dto.getUuid()));
        if (user == null) {
            user = mapParticipationDto(dto);
        } else {
            user.setPassword(dto.getPassword());
        }
        return userRepository.save(user);
    }

    public void createContest(CodingcontestDto dto) {
        UUID id = UUID.fromString(dto.getUuid());
        Challenge challenge = challengeRepository.findOne(id);
        if (challenge == null) {
            challenge.setId(id);
            challenge.setName(dto.getName());
            challenge.setCanonicalName("ccc-" + dto.getLocation() + "-" + dto.getGameName());
            // TODO fetch correct challenge template
            challenge.setChallengeTemplate(challengeTemplateRepository.findOneByNick(dto.getGameName()));
            challenge.setStartDate(ZonedDateTime.ofInstant(dto.getStartTime().toInstant(), ZoneId.systemDefault()));
            challenge = challengeRepository.save(challenge);
        }
        for (ParticipationDto participation : dto.getParticipations()) {
            User user = mapParticipationDto(participation);
            user.addInvitedChallenge(challenge);
            userRepository.save(user);
        }
    }

    public ContestInfoDto getResults(UUID id) {
        Challenge challenge = challengeRepository.findOneWithTemplate(id);
        if (challenge == null) {
            return null;
        }
        ContestInfoDto contestInfoDto = new ContestInfoDto();
        contestInfoDto.setUuid(id);
        contestInfoDto.setDurationHours(4);
        contestInfoDto.setDurationMinutes(4 * 60);
        contestInfoDto.setFailedTestPenalty(0);
        contestInfoDto.setUploadedCodePerLevelBonus(0);
        contestInfoDto.setGameName(challenge.getChallengeTemplate().getName());
        contestInfoDto.setName(challenge.getName());
        // TODO replace with getLeaderboard
        List<Result> results = resultRepository.findAllByChallenge(challenge.getId());
        List<ContestantDto> contestants = new ArrayList<>();
        for (Result result : results) {
            ContestantDto contestant = new ContestantDto();
            contestant.setEmail(result.getUser().getEmail());
            contestant.setUuid(result.getUser().getId());
            contestant.setLevelsCompleted(result.getStartTimes() == null ? 0 : result.getStartTimes().size());
            // TODO fill with other data when TaskResults are added
        }

        return contestInfoDto;
    }

    private User mapParticipationDto(ParticipationDto dto) {
        User user = new User();
        user.setId(UUID.fromString(dto.getUuid()));
        user.setUsername(dto.getName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        return user;
    }
}
