package uno.cod.platform.server.core.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "location")
public class Location {
    /***
     * A Google Place ID.
     * @see <a href="https://developers.google.com/places/place-id">Place IDs</a>
     */
    @Id
    private String id;

    private Float latitude;
    private Float longitude;

    @OneToMany(mappedBy = "key.location")
    private Set<LocationDetail> details;

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
}
