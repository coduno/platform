package uno.cod.platform.server.core.dto.location;

import uno.cod.platform.server.core.domain.Location;

import java.util.UUID;

public class LocationShowDto {
    private UUID id;
    private String name;
    private String placeId;
    private String latitude;
    private String longitude;

    public LocationShowDto(Location location) {
        this.id = location.getId();
        this.name = location.getName();
        this.placeId = location.getPlaceId();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
