package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.ActivationToken;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.*;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.exception.CodunoResourceConflictException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ActivationTokenService activationTokenService;
    private final ChallengeRepository challengeRepository;
    private final MailService mailService;

    @Value("#{T(java.time.Duration).parse('${coduno.register.expire}')}")
    private Duration duration;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       ActivationTokenService activationTokenService,
                       ChallengeRepository challengeRepository,
                       MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.activationTokenService = activationTokenService;
        this.challengeRepository = challengeRepository;
        this.mailService = mailService;
    }

    public void createFromDto(UserCreateDto dto) throws MessagingException {
        User found = userRepository.findByUsernameOrEmail(dto.getNick(), dto.getEmail());
        if (found != null) {
            throw new CodunoResourceConflictException("user.name.exists", new String[]{dto.getNick()});
        }

        Challenge challenge = null;
        if(!dto.getChallengeCanonicalName().isEmpty()) {
            challenge = challengeRepository.findOneByCanonicalName(dto.getChallengeCanonicalName());
            if (challenge == null) {
                throw new CodunoNoSuchElementException("challenge.invalid");
            }
        }

        String token = activationTokenService.createToken(dto.getEmail(), dto.getNick(),
                passwordEncoder.encode(dto.getPassword()), challenge, ZonedDateTime.now().plus(duration));

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("challengeCanonicalName", challenge == null ? "" : challenge.getCanonicalName());
        mailService.sendMail(dto.getNick(), dto.getEmail(), "Account verification", "account-verification", params, Locale.ENGLISH);
    }

    public UserDetails confirm(UUID id) {
        ActivationToken activationToken = activationTokenService.findTokenById(id);

        if (activationToken == null) {
           throw new CodunoNoSuchElementException("token.invalid");
        }

        // String firstName = "";
        // String lastName = "";
        // user.setFirstName(dto.getFirstName());
        // user.setLastName(dto.getLastName());

        User user = new User();
        user.setUsername(activationToken.getUsername());
        user.setEmail(activationToken.getEmail());
        user.setPassword(passwordEncoder.encode(activationToken.getPassword()));
        user.setEnabled(true);

        User stored = userRepository.save(user);
        return stored;
    }

    public UserShowDto update(UserUpdateProfileDetailsDto dto, User user) {
        if (!dto.getUsername().equals(user.getUsername()) && userRepository.findByUsername(dto.getUsername()) != null) {
            throw new CodunoResourceConflictException("user.name.exists", new String[]{dto.getUsername()});
        }
        if (!dto.getEmail().equals(user.getEmail()) && userRepository.findByEmail(dto.getEmail()) != null) {
            throw new CodunoResourceConflictException("email.existing", new String[]{dto.getEmail()});
        }
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        return new UserShowDto(userRepository.save(user));
    }

    public void updatePassword(UserPasswordChangeDto dto, User user) {
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new CodunoIllegalArgumentException("password.old.invalid");
        }

        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new CodunoIllegalArgumentException("password.new.match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    public UserShowDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CodunoNoSuchElementException("user.invalid");
        }
        return new UserShowDto(user);
    }

    public CurrentUserDto findCurrentUser(UUID id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new CodunoNoSuchElementException("user.invalid");
        }
        return new CurrentUserDto(user);
    }

    public UserShowDto findOne(UUID id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new CodunoNoSuchElementException("user.invalid");
        }
        return new UserShowDto(user);
    }

    public List<UserShortShowDto> listUsers() {
        return userRepository.findAll().stream().map(UserShortShowDto::new).collect(Collectors.toList());
    }

    public List<UserShortShowDto> listUsersByUsernameContaining(String searchValue) {
        if (searchValue.length() <= 3) {
            throw new CodunoIllegalArgumentException("user.search.length.invalid");
        }

        return userRepository.findByUsernameContaining(searchValue)
                .stream()
                .map(UserShortShowDto::new)
                .collect(Collectors.toList());
    }

    public UserShortShowDto findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CodunoNoSuchElementException("user.invalid");
        }
        return new UserShortShowDto(user);
    }
}

