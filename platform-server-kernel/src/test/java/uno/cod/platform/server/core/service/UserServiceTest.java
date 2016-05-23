package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.*;
import uno.cod.platform.server.core.exception.ResourceConflictException;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class UserServiceTest {
    private UserService service;
    private UserRepository repository;
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception {
        this.repository = Mockito.mock(UserRepository.class);
        this.passwordEncoder = Mockito.mock(PasswordEncoder.class);

        this.service = new UserService(repository, passwordEncoder);
    }

    @Test
    //TODO we need a mapper so we can test the logic that maps the DTO to ENTITY
    //TODO like this the test is pointless
    public void createFromDto() throws Exception {
        User user = UserTestUtil.getUser("user", "email");
        UserCreateDto dto = UserTestUtil.getUserCreateDto("user", "email");

        Mockito.when(repository.findByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(null);
        Mockito.when(repository.save(user)).thenReturn(user);
        service.createFromDto(dto);

        Assert.assertEquals(user.getUsername(), dto.getNick());
    }

    @Test(expected = ResourceConflictException.class)
    public void createFromDtoAlreadyExisting() throws Exception {
        User user = UserTestUtil.getUser("user", "email");
        UserCreateDto dto = UserTestUtil.getUserCreateDto("user", "email");

        Mockito.when(repository.findByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(new User());
        service.createFromDto(dto);
    }

    @Test
    // TODO needs mapper test
    public void update() throws Exception {

        User user = UserTestUtil.getUser("user", "email");
        UserUpdateProfileDetailsDto dto = UserTestUtil.getUserUpdateProfileDetailsDto("user2", "email2");

        Mockito.when(repository.findByUsername(dto.getUsername())).thenReturn(null);
        Mockito.when(repository.findByEmail(dto.getEmail())).thenReturn(null);
        Mockito.when(repository.save(user)).thenReturn(user);

        UserShowDto showDto = service.update(dto, user);

        Assert.assertEquals(showDto.getId(), user.getId());
        Assert.assertEquals(showDto.getEmail(), user.getEmail());
        Assert.assertEquals(showDto.getUsername(), user.getUsername());
    }

    @Test(expected = ResourceConflictException.class)
    public void updateExistingUsername() throws Exception {
        User user = UserTestUtil.getUser("user2", "email2");
        UserUpdateProfileDetailsDto dto = UserTestUtil.getUserUpdateProfileDetailsDto("user", "email");

        Mockito.when(repository.findByUsername(dto.getUsername())).thenReturn(new User());
        Mockito.when(repository.findByEmail(dto.getEmail())).thenReturn(null);

        service.update(dto, user);
    }

    @Test(expected = ResourceConflictException.class)
    public void updateExistingEmail() throws Exception {
        UserUpdateProfileDetailsDto dto = UserTestUtil.getUserUpdateProfileDetailsDto("user2", "emaila");

        Mockito.when(repository.findByUsername(dto.getUsername())).thenReturn(null);
        Mockito.when(repository.findByEmail(dto.getEmail())).thenReturn(new User());

        User user = UserTestUtil.getUser("user2", "email2");
        service.update(dto, user);
    }

    @Test
    public void updatePassword() throws Exception {
        User user = UserTestUtil.getUser();
        UserPasswordChangeDto dto = UserTestUtil.getUpdatePasswordChangeDto("password", "lala-password");
        Mockito.when(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())).thenReturn(true);

        service.updatePassword(dto, user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updatePasswordOldInvalid() throws Exception {
        User user = UserTestUtil.getUser();
        UserPasswordChangeDto dto = UserTestUtil.getUpdatePasswordChangeDto("passwordasdf", "password");
        Mockito.when(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())).thenReturn(false);

        service.updatePassword(dto, user);
    }


    @Test(expected = IllegalArgumentException.class)
    public void updatePasswordOldNewMatching() throws Exception {
        User user = UserTestUtil.getUser();
        UserPasswordChangeDto dto = UserTestUtil.getUpdatePasswordChangeDto("password", "password");

        service.updatePassword(dto, user);
    }

    @Test
    public void findByUsername() throws Exception {
        User user = UserTestUtil.getUser();
        Mockito.when(repository.findByUsername(user.getUsername())).thenReturn(user);

        UserShowDto showDto = service.findByUsername(user.getUsername());


        Assert.assertEquals(showDto.getId(), user.getId());
        Assert.assertEquals(showDto.getEmail(), user.getEmail());
        Assert.assertEquals(showDto.getUsername(), user.getUsername());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByUsernameNotExisting() throws Exception {
        Mockito.when(repository.findByUsername("user")).thenReturn(null);

        service.findByUsername("user");
    }

    @Test
    public void findOne() throws Exception {
        User user = UserTestUtil.getUser();
        Mockito.when(repository.findOne(user.getId())).thenReturn(user);

        UserShowDto showDto = service.findOne(user.getId());


        Assert.assertEquals(showDto.getId(), user.getId());
        Assert.assertEquals(showDto.getEmail(), user.getEmail());
        Assert.assertEquals(showDto.getUsername(), user.getUsername());
    }

    @Test(expected = NoSuchElementException.class)
    public void findOneNotExisting() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findOne(id)).thenReturn(null);

        service.findOne(id);
    }


    @Test
    public void listUsers() throws Exception {
        User user = UserTestUtil.getUser();
        Mockito.when(repository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserShortShowDto> dtos = service.listUsers();

        Assert.assertEquals(dtos.size(), 1);
        UserShortShowDto dto = dtos.get(0);
        Assert.assertEquals(dto.getId(), user.getId());
        Assert.assertEquals(dto.getUsername(), user.getUsername());
    }

    @Test
    public void listUsersByUsernameContaining() throws Exception {
        User user = UserTestUtil.getUser();
        String searchValue = "user";
        Mockito.when(repository.findByUsernameContaining(searchValue)).thenReturn(Collections.singletonList(user));

        List<UserShortShowDto> dtos = service.listUsersByUsernameContaining(searchValue);

        Assert.assertEquals(dtos.size(), 1);
        UserShortShowDto dto = dtos.get(0);
        Assert.assertEquals(dto.getId(), user.getId());
        Assert.assertEquals(dto.getUsername(), user.getUsername());
    }

    @Test(expected = IllegalArgumentException.class)
    public void listUsersByUsernameContainingSearchValueInvalid() throws Exception {
        service.listUsersByUsernameContaining("a");
    }

    @Test
    public void findByEmail() throws Exception {
        User user = UserTestUtil.getUser();
        Mockito.when(repository.findByEmail(user.getEmail())).thenReturn(user);

        UserShortShowDto showDto = service.findByEmail(user.getEmail());


        Assert.assertEquals(showDto.getId(), user.getId());
        Assert.assertEquals(showDto.getUsername(), user.getUsername());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByEmailExisting() throws Exception {
        Mockito.when(repository.findByEmail("user")).thenReturn(null);

        service.findByEmail("user");
    }
}