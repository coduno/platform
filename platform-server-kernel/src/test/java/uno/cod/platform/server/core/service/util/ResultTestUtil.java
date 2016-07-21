package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Result;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ResultTestUtil {
    public static Result getResult() {
        Result result = new Result(UUID.randomUUID());

        result.setChallenge(ChallengeTestUtil.getChallenge());
        result.setUser(UserTestUtil.getUser());
        result.setStarted(ZonedDateTime.now());
        result.setFinished(ZonedDateTime.now());

        return result;
    }
}
