package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.*;
import uno.cod.platform.server.core.exception.ResourceConflictException;
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

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createFromDto(UserCreateDto dto) {
        User found = repository.findByUsernameOrEmail(dto.getNick(), dto.getEmail());
        if (found != null) {
            throw new ResourceConflictException("user.name.exists");
        }
        User user = new User();
        user.setUsername(dto.getNick());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        repository.save(user);
    }

    public UserShowDto update(UserUpdateProfileDetailsDto dto, User user) {
        if (!dto.getUsername().equals(user.getUsername()) && repository.findByUsername(dto.getUsername()) != null) {
            throw new ResourceConflictException("username.existing");
        }
        if (!dto.getEmail().equals(user.getEmail()) && repository.findByEmail(dto.getEmail()) != null) {
            throw new ResourceConflictException("email.existing");
        }
        user.setUsername(dto.getUsername());
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
        User user = repository.findByUsername(username);
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

    public List<UserShowDto> listUsersLikeNameOrEmail(String searchValue){
        searchValue = "%" + searchValue + "%";
        return repository.findLikeUsernameOrEmail(searchValue)
                .stream()
                .map(UserShowDto::new)
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

