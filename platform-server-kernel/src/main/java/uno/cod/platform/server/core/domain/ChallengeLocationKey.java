package uno.cod.platform.server.core.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class ChallengeLocationKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(optional = false)
    private Challenge challenge;

    @NotNull
    @ManyToOne(optional = false)
    private Location location;

    ChallengeLocationKey() {
    }

    public ChallengeLocationKey(Challenge challenge, Location location) {
        this.challenge = challenge;
        this.location = location;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    private void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Location getLocation() {
        return location;
    }

    private void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChallengeLocationKey that = (ChallengeLocationKey) o;

        if (!challenge.equals(that.challenge)) {
            return false;
        }
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        int result = challenge.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }
}
