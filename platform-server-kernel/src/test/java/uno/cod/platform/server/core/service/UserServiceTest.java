package uno.cod.platform.server.core.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.UserPasswordChangeDto;
import uno.cod.platform.server.core.dto.user.UserShortShowDto;
import uno.cod.platform.server.core.dto.user.UserShowDto;
import uno.cod.platform.server.core.dto.user.UserUpdateProfileDetailsDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.exception.CodunoResourceConflictException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserService service;
    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private MailService mailService;
    private ChallengeRepository challengeRepository;

    @Before
    public void setUp() throws Exception {
        this.repository = mock(UserRepository.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.mailService = mock(MailService.class);
        this.challengeRepository = mock(ChallengeRepository.class);

        this.service = new UserService(repository, passwordEncoder, challengeRepository, mailService);
    }

    @Test
    //TODO we need a mapper so we can test the logic that maps the DTO to ENTITY
    //TODO like this the test is pointless
    public void createFromDto() throws Exception {
        final String username = "user";
        final String email = "email@foo.com";
        final String password = "password";

        User user = UserTestUtil.getUser(username, email);

        when(repository.findByCanonicalNameOrEmail(user.getUsername(), user.getEmail())).thenReturn(null);
        when(repository.save(user)).thenReturn(user);
        service.createUser(username, email, password);

        assertEquals(user.getUsername(), username);
    }

    @Test(expected = CodunoResourceConflictException.class)
    public void createFromDtoAlreadyExisting() throws Exception {
        final String username = "user";
        final String email = "email@foo.com";
        final String password = "password";

        User user = UserTestUtil.getUser(username, email);

        when(repository.findByCanonicalNameOrEmail(user.getUsername(), user.getEmail())).thenReturn(new User());
        when(repository.save(user)).thenReturn(user);
        service.createUser(username, email, password);

        assertEquals(user.getUsername(), username);
    }

    @Test
    // TODO needs mapper test
    public void update() throws Exception {

        User user = UserTestUtil.getUser("user", "email");
        UserUpdateProfileDetailsDto dto = UserTestUtil.getUserUpdateProfileDetailsDto("user2", "email2");

        when(repository.findByCanonicalName(dto.getUsername())).thenReturn(null);
        when(repository.findByEmail(dto.getEmail())).thenReturn(null);
        when(repository.save(user)).thenReturn(user);

        UserShowDto showDto = service.update(dto, user);

        assertEquals(showDto.getId(), user.getId());
        assertEquals(showDto.getEmail(), user.getEmail());
        assertEquals(showDto.getUsername(), user.getUsername());
    }

    @Test(expected = CodunoResourceConflictException.class)
    public void updateExistingUsername() throws Exception {
        User user = UserTestUtil.getUser("user2", "email2");
        UserUpdateProfileDetailsDto dto = UserTestUtil.getUserUpdateProfileDetailsDto("user", "email");

        when(repository.findByCanonicalName(dto.getUsername())).thenReturn(new User());
        when(repository.findByEmail(dto.getEmail())).thenReturn(null);

        service.update(dto, user);
    }

    @Test(expected = CodunoResourceConflictException.class)
    public void updateExistingEmail() throws Exception {
        UserUpdateProfileDetailsDto dto = UserTestUtil.getUserUpdateProfileDetailsDto("user2", "emaila");

        when(repository.findByCanonicalName(dto.getUsername())).thenReturn(null);
        when(repository.findByEmail(dto.getEmail())).thenReturn(new User());

        User user = UserTestUtil.getUser("user2", "email2");
        service.update(dto, user);
    }

    @Test
    public void updatePassword() throws Exception {
        User user = UserTestUtil.getUser();
        UserPasswordChangeDto dto = UserTestUtil.getUpdatePasswordChangeDto("password", "lala-password");
        when(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())).thenReturn(true);

        service.updatePassword(dto, user);
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void updatePasswordOldInvalid() throws Exception {
        User user = UserTestUtil.getUser();
        UserPasswordChangeDto dto = UserTestUtil.getUpdatePasswordChangeDto("passwordasdf", "password");
        when(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())).thenReturn(false);

        service.updatePassword(dto, user);
    }


    @Test(expected = CodunoIllegalArgumentException.class)
    public void updatePasswordOldNewMatching() throws Exception {
        User user = UserTestUtil.getUser();
        UserPasswordChangeDto dto = UserTestUtil.getUpdatePasswordChangeDto("password", "password");

        service.updatePassword(dto, user);
    }

    @Test
    public void findByUsername() throws Exception {
        User user = UserTestUtil.getUser();
        when(repository.findByCanonicalName(user.getUsername())).thenReturn(user);

        UserShowDto showDto = service.findByUsername(user.getUsername());


        assertEquals(showDto.getId(), user.getId());
        assertEquals(showDto.getEmail(), user.getEmail());
        assertEquals(showDto.getUsername(), user.getUsername());
    }

    @Test(expected = CodunoNoSuchElementException.class)
    public void findByUsernameNotExisting() throws Exception {
        when(repository.findByCanonicalName("user")).thenReturn(null);

        service.findByUsername("user");
    }

    @Test
    public void findOne() throws Exception {
        User user = UserTestUtil.getUser();
        when(repository.findOne(user.getId())).thenReturn(user);

        UserShowDto showDto = service.findOne(user.getId());


        assertEquals(showDto.getId(), user.getId());
        assertEquals(showDto.getEmail(), user.getEmail());
        assertEquals(showDto.getUsername(), user.getUsername());
    }

    @Test(expected = CodunoNoSuchElementException.class)
    public void findOneNotExisting() throws Exception {
        UUID id = UUID.randomUUID();
        when(repository.findOne(id)).thenReturn(null);

        service.findOne(id);
    }


    @Test
    public void listUsers() throws Exception {
        User user = UserTestUtil.getUser();
        when(repository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserShortShowDto> dtos = service.listUsers();

        assertEquals(dtos.size(), 1);
        UserShortShowDto dto = dtos.get(0);
        assertEquals(dto.getId(), user.getId());
        assertEquals(dto.getUsername(), user.getUsername());
    }

    @Test
    public void listUsersByUsernameContaining() throws Exception {
        User user = UserTestUtil.getUser();
        String searchValue = "user";
        when(repository.findByCanonicalNameContaining(searchValue)).thenReturn(Collections.singletonList(user));

        List<UserShortShowDto> dtos = service.listUsersByUsernameContaining(searchValue);

        assertEquals(dtos.size(), 1);
        UserShortShowDto dto = dtos.get(0);
        assertEquals(dto.getId(), user.getId());
        assertEquals(dto.getUsername(), user.getUsername());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void listUsersByUsernameContainingSearchValueInvalid() throws Exception {
        service.listUsersByUsernameContaining("a");
    }

    @Test
    public void findByEmail() throws Exception {
        User user = UserTestUtil.getUser();
        when(repository.findByEmail(user.getEmail())).thenReturn(user);

        UserShortShowDto showDto = service.findByEmail(user.getEmail());


        assertEquals(showDto.getId(), user.getId());
        assertEquals(showDto.getUsername(), user.getUsername());
    }

    @Test(expected = CodunoNoSuchElementException.class)
    public void findByEmailExisting() throws Exception {
        when(repository.findByEmail("user")).thenReturn(null);

        service.findByEmail("user");
    }
}