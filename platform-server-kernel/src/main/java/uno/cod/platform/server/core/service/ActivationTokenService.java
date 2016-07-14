package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.ActivationToken;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.repository.ActivationTokenRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

/**
 * Created by drogetzer on 12.07.2016.
 */
@Service
@Transactional
public class ActivationTokenService {

    ActivationTokenRepository activationTokenRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    Random random;

    @Autowired
    public ActivationTokenService(ActivationTokenRepository activationTokenRepository,
                                  UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        this.activationTokenRepository = activationTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        random = new Random();
    }

    public String createToken(String email, String username, String password, Challenge challenge, ZonedDateTime expire) {
        String token = new BigInteger(130, random).toString(32);
        ActivationToken activationToken = new ActivationToken();
        activationToken.setEmail(email);
        activationToken.setUsername(username);
        activationToken.setPassword(password);
        activationToken.setChallenge(challenge);
        activationToken.setExpire(expire);
        activationToken.setToken(passwordEncoder.encode(token));

        ActivationToken stored = activationTokenRepository.save(activationToken);

        byte[] bytes = (stored.getId().toString() + ":" + token).getBytes();
        return new String(Base64.encode(bytes));
    }


    public  ActivationToken findTokenById(UUID id) {
        return activationTokenRepository.findOneById(id);
    }

    public ActivationToken findTokenByEmail(String email) {
        return activationTokenRepository.findOneByEmail(email);
    }

    public void deleteActivationToken(UUID id) {
        activationTokenRepository.delete(id);
    }

    public UserDetails loadByActivationToken(UUID id, String token) {

        ActivationToken activationToken = activationTokenRepository.findOneById(id);
        if (activationToken == null) {
            throw new CodunoNoSuchElementException("token.invalid");
        }

        if (!passwordEncoder.matches(token, activationToken.getToken())) {
            return null;
        }

        deleteActivationToken(id);

        // Make user a real user
        User user = new User();
        user.setUsername(activationToken.getUsername());
        user.setEmail(activationToken.getEmail());
        user.setPassword(passwordEncoder.encode(activationToken.getPassword()));
        user.setEnabled(true);

        return userRepository.save(user);
    }
}
