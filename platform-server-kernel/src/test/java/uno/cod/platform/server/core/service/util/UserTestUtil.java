package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.CanonicalName;
import uno.cod.platform.server.core.domain.User;

import java.util.UUID;

public class UserTestUtil {
    public static User getUser() {
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail("email");
        CanonicalName username = new CanonicalName();
        username.setValue("username");
        user.setCanonicalName(username);
        user.setPassword("password");

        return user;
    }
}
