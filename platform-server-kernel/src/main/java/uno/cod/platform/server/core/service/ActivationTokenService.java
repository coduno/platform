package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.ActivationToken;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.repository.ActivationTokenRepository;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by drogetzer on 12.07.2016.
 */
@Service
@Transactional
public class ActivationTokenService {

    ActivationTokenRepository activationTokenRepository;

    @Autowired
    public ActivationTokenService(ActivationTokenRepository activationTokenRepository) {
        this.activationTokenRepository = activationTokenRepository;
    }

    public UUID createToken(String email, String username, String password, Challenge challenge, ZonedDateTime expire) {
        ActivationToken activationToken = new ActivationToken();
        activationToken.setEmail(email);
        activationToken.setUsername(username);
        activationToken.setPassword(password);
        activationToken.setChallenge(challenge);
        activationToken.setExpire(expire);
        return activationTokenRepository.save(activationToken).getId();
    }

    public  ActivationToken findTokenById(UUID id) {
        return activationTokenRepository.findOneById(id);
    }

    public ActivationToken findTokenByEmail(String email) {
        return activationTokenRepository.findOneByEmail(email);
    }
}
