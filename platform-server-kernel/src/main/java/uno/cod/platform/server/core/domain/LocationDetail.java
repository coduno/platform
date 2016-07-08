package uno.cod.platform.server.core.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(name = "location_detail")
@AssociationOverrides({
        @AssociationOverride(name = "key.challenge", joinColumns = {@JoinColumn(name = "challenge_id")}),
        @AssociationOverride(name = "key.location", joinColumns = {@JoinColumn(name = "location_id")})
})
public class LocationDetail {
    @EmbeddedId
    private ChallengeLocationKey key = new ChallengeLocationKey();

    @NotEmpty
    private String name;

    @NotNull
    private String description;

    @NotEmpty
    private String address;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    public ChallengeLocationKey getKey() {
        return this.key;
    }

    public void setKey(ChallengeLocationKey key) {
        this.key = key;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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