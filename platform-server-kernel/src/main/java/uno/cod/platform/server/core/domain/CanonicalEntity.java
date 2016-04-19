package uno.cod.platform.server.core.domain;

import java.util.UUID;

public interface CanonicalEntity {
    UUID getId();
    String getCanonicalName();
}
