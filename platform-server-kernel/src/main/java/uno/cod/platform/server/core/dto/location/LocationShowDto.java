package uno.cod.platform.server.core.dto.location;

import uno.cod.platform.server.core.domain.Location;

import java.util.UUID;

public class LocationShowDto {
    private UUID id;
    private String name;

    public LocationShowDto(Location location) {
        this.id = location.getId();
        this.name = location.getName();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
