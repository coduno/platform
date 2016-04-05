package uno.cod.platform.server.codingcontest.sync.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.codingcontest.sync.dto.*;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ChallengeTemplateRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
            user = createUserFromDto(dto);
        } else {
            user.setPassword(dto.getPassword());
        }
        return userRepository.save(user);
    }

    public void createOrUpdateContest(CodingcontestDto dto) {
        UUID id = UUID.fromString(dto.getUuid());
        Challenge challenge = challengeRepository.findOne(id);
        ChallengeTemplate template = challengeTemplateRepository.findOneByName(dto.getGameName());
        if (template == null) {
            throw new IllegalArgumentException("sync.game.invalid");
        }
        if (challenge == null) {
            challenge = new Challenge();
            challenge.setId(id);
            challenge.setName(dto.getName());
            challenge.setCanonicalName("ccc-" + dto.getLocation() + "-" + Year.now().toString());
            challenge.setChallengeTemplate(template);
            challenge.setStartDate(ZonedDateTime.ofInstant(dto.getStartTime().toInstant(), ZoneId.of("UTC")));
            challenge = challengeRepository.save(challenge);
        }

        for (ParticipationDto participation : dto.getParticipations()) {
            User user = userRepository.findOne(UUID.fromString(dto.getUuid()));
            if (user == null) {
                user = createUserFromDto(participation);
                user.addInvitedChallenge(challenge);
            } else {
                user.setPassword(participation.getPassword());
                if(!user.getInvitedChallenges().contains(challenge)) {
                    user.addInvitedChallenge(challenge);
                }
            }
            user.setEnabled(true);
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
        List<Object[]> results = resultRepository.findLeaderboardForChallenge(challenge.getId());
        List<ContestantDto> contestants = new ArrayList<>();
        int rank = 0;
        for (Object[] resultRow: results) {
            Result result = (Result) resultRow[0];
            int finishedTasks = ((Long) resultRow[1]).intValue();

            ContestantDto contestant = new ContestantDto();
            contestant.setEmail(result.getUser().getEmail());
            contestant.setUuid(result.getUser().getId());
            contestant.setCodingLanguage(findLanguage(result));
            contestant.setFinished(result.getFinished() != null);
            contestant.setLevelsCompleted(finishedTasks);
            contestant.setFailedTests(countFailedTests(result));
            contestant.setRank(++rank);
            contestant.setResults(getContestResults(result));
            contestants.add(contestant);
        }
        contestInfoDto.setParticipations(contestants);
        return contestInfoDto;
    }

    private List<ContestResultDto> getContestResults(Result result){
        List<Task> tasks = result.getChallenge().getChallengeTemplate().getTasks();
        List<ContestResultDto> contestResults = new ArrayList<>();

        for (TaskResult taskResult: result.getTaskResults()){
            int failedTests = countFailedTests(taskResult);
            ContestResultDto contestResultDto = new ContestResultDto();
            contestResultDto.setFailedTests(failedTests);
            contestResultDto.setCodeUploaded(taskResult.getSubmissions() != null && !taskResult.getSubmissions().isEmpty());
            Long finishTime = null;
            if (taskResult.getEndTime() != null){
                finishTime = ChronoUnit.MILLIS.between(taskResult.getStartTime(), taskResult.getEndTime());
            }
            contestResultDto.setFinishTime(finishTime);
            contestResultDto.setLevel(tasks.indexOf(taskResult.getKey().getTask()) + 1);
            contestResults.add(contestResultDto);
        }
        return contestResults;
    }

    private int countFailedTests(TaskResult taskResult){
        int failed = 0;
        for (Submission submission: taskResult.getSubmissions()){
            for (TestResult testResult: submission.getTestResults()){
                if (!testResult.isGreen()){
                    failed++;
                }
            }
        }
        return failed;
    }

    private int countFailedTests(Result result){
        if (result.getTaskResults() == null || result.getTaskResults().isEmpty()){
            return 0;
        }
        int failed = 0;
        for (TaskResult taskResult: result.getTaskResults()){
            failed += countFailedTests(taskResult);
        }
        return failed;
    }

    private String findLanguage(Result result){
        if (result.getTaskResults() == null || result.getTaskResults().isEmpty()){
            return null;
        }
        for (TaskResult taskResult: result.getTaskResults()){
            for (Submission submission: taskResult.getSubmissions()){
                if (submission.getLanguage() != null){
                    return submission.getLanguage().getName();
                }
            }
        }
        return null;
    }

    private User createUserFromDto(ParticipationDto dto) {
        User user = new User();
        user.setId(UUID.fromString(dto.getUuid()));
        user.setUsername(dto.getName());
        user.setPassword(dto.getPassword());
        if(dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        } else {
            user.setEmail(dto.getUuid() + "@team.cod.uno");
        }
        return user;
    }
}
