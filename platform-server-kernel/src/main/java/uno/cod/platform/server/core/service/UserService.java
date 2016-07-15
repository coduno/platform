package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.*;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.exception.CodunoResourceConflictException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final MailService mailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       ChallengeRepository challengeRepository,
                       MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.challengeRepository = challengeRepository;
        this.mailService = mailService;
    }

    public User createUser(String username, String email, String password) {
        return createUser(username, email, password, null, null);
    }

    public User createUser(String username, String email, String password, String firstName, String lastName) {
        User found = userRepository.findByCanonicalNameOrEmail(username, email);

        if (found != null) {
            throw new CodunoResourceConflictException("user.name.exists", new String[]{username});
        }

        return userRepository.save(new User(username, email, passwordEncoder.encode(password), firstName, lastName));
    }

    public UserShowDto update(UserUpdateProfileDetailsDto dto, User user) {
        if (!dto.getUsername().equals(user.getUsername()) && userRepository.findByCanonicalName(dto.getUsername()) != null) {
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
        User user = userRepository.findByCanonicalName(username);
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

        return userRepository.findByCanonicalNameContaining(searchValue)
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

