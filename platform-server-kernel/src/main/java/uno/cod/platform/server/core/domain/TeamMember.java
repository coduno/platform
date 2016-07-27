package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "team_member")
@AssociationOverrides({
        @AssociationOverride(name = "key.user", joinColumns = {@JoinColumn(name = "user_id")}),
        @AssociationOverride(name = "key.team", joinColumns = {@JoinColumn(name = "team_id")})
})
public class TeamMember implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TeamUserKey key = new TeamUserKey();

    @Column(nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    private boolean admin;

    public TeamMember(Team team, User user) {
        this(team, user, false);
    }

    public TeamMember(Team team, User user, boolean admin) {
        this(new TeamUserKey(team, user), admin);
    }

    public TeamMember(TeamUserKey key) {
        this(key, false);
    }

    public TeamMember(TeamUserKey key, boolean admin) {
        this.key = key;
        this.admin = admin;
    }

    protected TeamMember() {
    }

    public TeamUserKey getKey() {
        return this.key;
    }

    protected void setKey(TeamUserKey key) {
        this.key = key;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    protected void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    protected void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
