package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * A participation is always a relation between
 * a user and a challenge. If the user participates
 * within a team (and thus the results/submissions
 * will be handled differently), the `team` member
 * variable is not null.
 */
@Entity
@Table(name = "participation")
@AssociationOverrides({
        @AssociationOverride(name = "key.user", joinColumns = {@JoinColumn(name = "user_id")}),
        @AssociationOverride(name = "key.challenge", joinColumns = {@JoinColumn(name = "challenge_id")})
})
public class Participation {
    @EmbeddedId
    private ParticipationKey key = new ParticipationKey();

    @ManyToOne
    private Team team;

    @ManyToOne
    private Location location;

    @Column(nullable = false, updatable = false)
    private Date created = new Date();

    public ParticipationKey getKey() {
        return key;
    }

    public void setKey(ParticipationKey key) {
        this.key = key;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date registerDate) {
        this.created = registerDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Transient
    public boolean isOnline() {
        return this.location == null;
    }

    @Transient
    public boolean isTeam() {
        return this.team != null;
    }
}
