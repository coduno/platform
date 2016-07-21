package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Runner;

import java.util.UUID;

public class RunnerTestUtil {
    public static Runner getRunner() {
        return getRunner(UUID.randomUUID());
    }

    public static Runner getRunner(UUID id) {
        Runner runner = new Runner(id);
        runner.setPath("default runner");
        return runner;
    }
}
