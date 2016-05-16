package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "canonical_name")
public class CanonicalName {
    @Id
    @Pattern(regexp = "^[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*$")
    private String value;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @OneToOne(fetch = FetchType.LAZY)
    private Team team;
    @OneToOne(fetch = FetchType.LAZY)
    private Organization organization;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
