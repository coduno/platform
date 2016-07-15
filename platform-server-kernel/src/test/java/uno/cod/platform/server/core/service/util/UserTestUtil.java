package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.ActivationTokenCreateDto;
import uno.cod.platform.server.core.dto.user.UserPasswordChangeDto;
import uno.cod.platform.server.core.dto.user.UserUpdateProfileDetailsDto;

import java.util.UUID;

public class UserTestUtil {
    public static UserPasswordChangeDto getUpdatePasswordChangeDto(String old, String newpass) {
        UserPasswordChangeDto dto = new UserPasswordChangeDto();
        dto.setNewPassword(newpass);
        dto.setOldPassword(old);

        return dto;
    }

    public static ActivationTokenCreateDto getUserCreateDto() {
        ActivationTokenCreateDto user = getUserCreateDto("username", "password");
        user.setPassword("password");
        return user;
    }

    public static ActivationTokenCreateDto getUserCreateDto(String username, String email) {
        ActivationTokenCreateDto user = new ActivationTokenCreateDto();
        user.setNick(username);
        user.setEmail(email);
        user.setPassword("password");
        return user;
    }

    public static UserUpdateProfileDetailsDto getUserUpdateProfileDetailsDto(String username, String email) {
        UserUpdateProfileDetailsDto user = new UserUpdateProfileDetailsDto();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName("fn");
        user.setLastName("ln");
        return user;
    }

    public static User getUser() {
        User user = getUser("username", "email");
        user.setPassword("password");
        return user;
    }

    public static User getUser(String username, String email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword("password");
        return user;
    }
}
