package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.result.ResultShowDto;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import java.time.ZonedDateTime;

@Service
public class ResultService {
    private final ResultRepository repository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    @Autowired
    public ResultService(ResultRepository repository, UserRepository userRepository, ChallengeRepository challengeRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
    }

    public ResultShowDto save(Long challengeId, String username){
        User user = userRepository.findByUsernameOrEmail(username, username);
        if (user == null){
            throw new IllegalArgumentException("user.invalid");
        }
        Challenge challenge = challengeRepository.findOne(challengeId);
        if (challenge == null){
            throw new IllegalArgumentException("challenge.invalid");
        }
        Result result = new Result();
        user.addResult(result);
        challenge.addResult(result);
        result.setStarted(ZonedDateTime.now());

        return new ResultShowDto(repository.save(result));
    }

    public ResultShowDto findOne(Long id){
        return new ResultShowDto(repository.findOne(id));
    }
}
