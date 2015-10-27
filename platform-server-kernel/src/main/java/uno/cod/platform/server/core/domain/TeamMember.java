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
    @EmbeddedId
    private TeamMemberKey key = new TeamMemberKey();

    @Column(nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    private boolean admin = false;

    public TeamMemberKey getKey() {
        return this.key;
    }

    public void setKey(TeamMemberKey key) {
        this.key = key;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
