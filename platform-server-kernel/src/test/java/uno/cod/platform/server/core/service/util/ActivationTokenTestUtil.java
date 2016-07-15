package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.ActivationToken;

import java.time.ZonedDateTime;

public class ActivationTokenTestUtil {

    public static ActivationToken getActivationToken() {
        ActivationToken invitation = new ActivationToken();

        invitation.setToken("token");
        invitation.setExpire(ZonedDateTime.now());
        invitation.setEmail("email");
        invitation.setChallenge(ChallengeTestUtil.getChallenge());

        return invitation;
    }
}
