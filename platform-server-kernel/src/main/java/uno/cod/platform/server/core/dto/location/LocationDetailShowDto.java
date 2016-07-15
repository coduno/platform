package uno.cod.platform.server.core.dto.location;

import uno.cod.platform.server.core.domain.LocationDetail;

public class LocationDetailShowDto {
    private String id;
    private Float latitude;
    private Float longitude;

    private String name;
    private String description;
    private String address;

    public LocationDetailShowDto(LocationDetail locationDetail) {
        this.name = locationDetail.getName();
        this.description = locationDetail.getDescription();
        this.address = locationDetail.getAddress();
        this.id = locationDetail.getKey().getLocation().getId();
        this.latitude = locationDetail.getKey().getLocation().getLatitude();
        this.longitude = locationDetail.getKey().getLocation().getLongitude();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
