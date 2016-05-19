package uno.cod.platform.server.core.domain;

import javax.persistence.*;

@Entity
@Table(name = "participation")
public class Participation extends IdentifiableEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    private Challenge challenge;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    private Team team;

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
        this.challenge.addParticipation(this);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if(this.team!=null){
            throw new IllegalArgumentException("participation.invalid");
        }
        this.user = user;
        this.user.addParticipation(this);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        if(this.user!=null){
            throw new IllegalArgumentException("participation.invalid");
        }
        this.team = team;
        this.team.addParticipation(this);
    }
}
