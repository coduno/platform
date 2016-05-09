package uno.cod.platform.server.core.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class TeamUserKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Team team;

    @ManyToOne
    private User user;

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TeamUserKey that = (TeamUserKey) o;

        if (team != null ? !team.equals(that.team) : that.team != null) {
            return false;
        }
        return !(user != null ? !user.equals(that.user) : that.user != null);
    }

    @Override
    public int hashCode() {
        int result = team != null ? team.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}