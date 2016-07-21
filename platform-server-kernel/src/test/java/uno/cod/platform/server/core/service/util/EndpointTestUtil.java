package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Endpoint;

import java.util.UUID;

public class EndpointTestUtil {
    public static Endpoint getEndpoint() {
        return getEndpoint(UUID.randomUUID());
    }

    public static Endpoint getEndpoint(UUID id) {
        Endpoint endpoint = new Endpoint(id);
        endpoint.setName("endpoint");
        endpoint.setComponent("endpoint-element");
        return endpoint;
    }
}
