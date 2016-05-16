package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.CanonicalName;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.*;
import uno.cod.platform.server.core.exception.ResourceConflictException;
import uno.cod.platform.server.core.repository.CanonicalNameRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final CanonicalNameRepository canonicalNameRepository;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, CanonicalNameRepository canonicalNameRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.canonicalNameRepository = canonicalNameRepository;
    }

    public void createFromDto(UserCreateDto dto) {
        if (repository.findByEmail(dto.getEmail()) != null) {
            throw new ResourceConflictException("user.email.exists");
        }
        if (canonicalNameRepository.findOne(dto.getNick()) != null) {
            throw new ResourceConflictException("user.name.exists");
        }
        User user = new User();
        CanonicalName username = new CanonicalName();
        username.setValue(dto.getNick());
        user.setCanonicalName(username);
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        repository.save(user);
    }

    public UserShowDto update(UserUpdateProfileDetailsDto dto, User user) {
        if (!dto.getUsername().equals(user.getUsername()) && repository.findByUsernameValue(dto.getUsername()) != null) {
            throw new ResourceConflictException("user.name.exists");
        }
        if (!dto.getEmail().equals(user.getEmail()) && repository.findByEmail(dto.getEmail()) != null) {
            throw new ResourceConflictException("email.existing");
        }
//        user.setCanonicalName(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        return new UserShowDto(repository.save(user));
    }

    public void updatePassword(UserPasswordChangeDto dto, User user) {
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new IllegalArgumentException("new.password.matches.old");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("old.password.invalid");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(user);
    }

    public UserShowDto findByUsername(String username) {
        User user = repository.findByUsernameValue(username);
        if (user == null) {
            throw new NoSuchElementException("user.invalid");
        }
        return new UserShowDto(user);
    }

    public UserShowDto findOne(UUID id) {
        User user = repository.findOne(id);
        if (user == null) {
            throw new NoSuchElementException("user.invalid");
        }
        return new UserShowDto(user);
    }

    public List<UserShortShowDto> listUsers() {
        return repository.findAll().stream().map(UserShortShowDto::new).collect(Collectors.toList());
    }

    public List<UserShortShowDto> listUsersByUsernameContaining(String searchValue) {
        if (searchValue.length() <= 3) {
            throw new IllegalArgumentException("user.search.length.invalid");
        }

        return repository.findByUsernameValueContaining(searchValue)
                .stream()
                .map(UserShortShowDto::new)
                .collect(Collectors.toList());
    }

    public UserShortShowDto findByEmail(String email) {
        User user = repository.findByEmail(email);
        if (user == null) {
            throw new NoSuchElementException("user.invalid");
        }
        return new UserShortShowDto(user);
    }
}

