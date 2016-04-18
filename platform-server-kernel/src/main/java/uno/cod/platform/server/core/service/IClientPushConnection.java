package uno.cod.platform.server.core.service;

import uno.cod.platform.server.core.domain.CanonicalEntity;
import uno.cod.platform.server.core.domain.Task;

public interface IClientPushConnection {
    void send(CanonicalEntity entity, String message);
    void sendLevelCompleted(CanonicalEntity entity, Task task);
}
