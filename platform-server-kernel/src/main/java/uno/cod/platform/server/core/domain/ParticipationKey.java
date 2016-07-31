package uno.cod.platform.server.core.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class ParticipationKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Challenge challenge;

    @ManyToOne
    private User user;

    public ParticipationKey(Challenge challenge, User user) {
        this.challenge = challenge;
        this.user = user;
    }

    protected ParticipationKey() {
    }

    public Challenge getChallenge() {
        return challenge;
    }

    protected void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public User getUser() {
        return user;
    }

    protected void setUser(User user) {
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

        ParticipationKey that = (ParticipationKey) o;

        if (!challenge.equals(that.challenge)) {
            return false;
        }
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = challenge.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }
}
