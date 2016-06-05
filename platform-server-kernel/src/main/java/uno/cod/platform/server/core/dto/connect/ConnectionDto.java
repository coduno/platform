package uno.cod.platform.server.core.dto.connect;

import org.springframework.social.connect.Connection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConnectionDto {
    private Set<String> providers;
    private Map<String, List<ConnectionDataDto>> connections;

    public Set<String> getProviders() {
        return providers;
    }

    public void setProviders(Set<String> providers) {
        this.providers = providers;
    }

    public Map<String, List<ConnectionDataDto>> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, List<Connection<?>>> connections) {
        this.connections = new HashMap<>();
        for (Map.Entry<String, List<Connection<?>>> entry : connections.entrySet()) {
            String key = entry.getKey();
            List<ConnectionDataDto> value = entry.getValue().stream().map(ConnectionDataDto::new).collect(Collectors.toList());
            this.connections.put(key, value);
        }
    }
}
