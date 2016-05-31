package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.challenge.LeaderboardEntryDto;
import uno.cod.platform.server.core.dto.result.ResultInfoDto;
import uno.cod.platform.server.core.dto.result.ResultShowDto;
import uno.cod.platform.server.core.dto.user.UserShortShowDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.mapper.ResultMapper;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ResultRepository;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ResultService {
    private final ResultRepository repository;
    private final ChallengeRepository challengeRepository;
    private final TaskScheduler taskScheduler;

    @Autowired
    public ResultService(ResultRepository repository,
                         ChallengeRepository challengeRepository, TaskScheduler taskScheduler) {
        this.repository = repository;
        this.challengeRepository = challengeRepository;
        this.taskScheduler = taskScheduler;
    }

    public ResultShowDto save(UUID challengeId, User user) {
        Challenge challenge = challengeRepository.findOneWithTemplate(challengeId);
        if (challenge == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }
        Result result = repository.findOneByUserAndChallenge(user.getId(), challengeId);
        if (result != null) {
            throw new CodunoIllegalArgumentException("challenge.completed");
        }
        result = new Result();
        challenge.addResult(result);
        result.setStarted(ZonedDateTime.now());
        result.setUser(user);
        result = repository.save(result);
        final UUID resultId = result.getId();

        Date setFinished = null;
        if (challenge.getEndDate() != null) {
            setFinished = Date.from(challenge.getEndDate().toInstant());
        } else {
            setFinished = Date.from(result.getStarted().plus(challenge.getChallengeTemplate().getDuration()).toInstant());
        }

        taskScheduler.schedule(() -> {
                    Result r = repository.findOne(resultId);
                    if (r.getFinished() != null) {
                        return;
                    }
                    r.setFinished(ZonedDateTime.now());
                    repository.save(r);
        }, setFinished);

        return ResultMapper.map(result);
    }

    public ResultShowDto findOne(UUID id) {
        return ResultMapper.map(repository.findOne(id));
    }

    public ResultShowDto findOneByUserAndChallenge(UUID userId, UUID challengeId) {
        return ResultMapper.map(repository.findOneByUserAndChallenge(userId, challengeId));
    }

    public List<LeaderboardEntryDto> getLeaderboard(UUID challengeId) {
        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();
        List<Object[]> results = repository.findLeaderboardForChallenge(challengeId);
        for (Object[] resultEntry : results) {
            LeaderboardEntryDto entry = new LeaderboardEntryDto();
            Result result = (Result) resultEntry[0];
            entry.setUser(new UserShortShowDto(result.getUser()));
            entry.setTasksCompleted((Long) resultEntry[1]);
            entry.setLastLevelFinishDate((ZonedDateTime) resultEntry[2]);
            leaderboard.add(entry);
        }
        return leaderboard;
    }

    public ResultInfoDto getResultInfoForUserAndChallenge(UUID userId, UUID challengeId) {
        Result result = repository.findOneWithTaskResultsByUserAndChallenge(userId, challengeId);
        if (result == null) {
            return null;
        }
        return new ResultInfoDto(result);
    }
}
