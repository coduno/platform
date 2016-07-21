package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.ActivationToken;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.ActivationTokenCreateDto;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.exception.CodunoResourceConflictException;
import uno.cod.platform.server.core.repository.ActivationTokenRepository;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class ActivationTokenService {
    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final MailService mailService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Random random;

    @Value("#{T(java.time.Duration).parse('${coduno.register.expire}')}")
    private Duration duration;

    @Autowired
    public ActivationTokenService(ActivationTokenRepository activationTokenRepository,
                                  UserRepository userRepository,
                                  ChallengeRepository challengeRepository,
                                  MailService mailService,
                                  UserService userService,
                                  PasswordEncoder passwordEncoder) {
        this.activationTokenRepository = activationTokenRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.challengeRepository = challengeRepository;
        this.mailService = mailService;
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

    public ActivationToken findTokenById(UUID id) {
        return activationTokenRepository.findOne(id);
    }

    public void deleteActivationToken(UUID id) {
        activationTokenRepository.delete(id);
    }

    public UserDetails loadByActivationToken(UUID id, String token) {

        ActivationToken activationToken = activationTokenRepository.findOne(id);
        if (activationToken == null) {
            throw new CodunoNoSuchElementException("token.invalid");
        }

        if (!passwordEncoder.matches(token, activationToken.getToken())) {
            return null;
        }

        deleteActivationToken(id);

        User found = userRepository.findByCanonicalNameOrEmail(activationToken.getUsername(), activationToken.getEmail());
        if (found != null) {
           return found;
        }

        return userService.createUser(activationToken.getUsername(), activationToken.getEmail(), activationToken.getPassword());
    }

    public void createActivationTokenFromDto(ActivationTokenCreateDto dto) throws MessagingException {
        User found = userRepository.findByCanonicalNameOrEmail(dto.getNick(), dto.getEmail());
        if (found != null) {
            throw new CodunoResourceConflictException("user.name.exists", new String[]{dto.getNick()});
        }

        Challenge challenge = null;
        if (dto.getChallengeCanonicalName() != null && !dto.getChallengeCanonicalName().isEmpty()) {
            challenge = challengeRepository.findOneByCanonicalName(dto.getChallengeCanonicalName());
            if (challenge == null) {
                throw new CodunoNoSuchElementException("challenge.invalid");
            }
        }

        String token = createToken(dto.getEmail(), dto.getNick(),
                passwordEncoder.encode(dto.getPassword()), challenge, ZonedDateTime.now().plus(duration));

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("challengeCanonicalName", challenge == null ? "" : challenge.getCanonicalName());
        mailService.sendMail(dto.getNick(), dto.getEmail(), "Account verification", "account-verification", params, Locale.ENGLISH);
    }
}
