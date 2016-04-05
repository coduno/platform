package uno.cod.platform.server.core.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "challenge")
public class Challenge extends IdentifiableEntity{
    private String name;

    @NotNull
    @NotEmpty
    @Column(name = "canonical_name", unique = true, nullable = false)
    private String canonicalName;

    @ManyToOne
    @JoinColumn(name = "challenge_template_id")
    private ChallengeTemplate challengeTemplate;

    @ManyToMany
    @JoinTable(
            name = "challenge_user",
            joinColumns = {@JoinColumn(name = "invited_challenge_id")},
            inverseJoinColumns = {@JoinColumn(name = "invited_user_id")}
    )
    private Set<User> invitedUsers;

    @OneToMany(mappedBy = "challenge")
    private Set<Result> results;

    @Column(name = "invite_only")
    private boolean inviteOnly = true;

    /**
     * Start of the challenge, users can already be invited before
     */
    @Column(name = "start_date")
    private ZonedDateTime startDate;

    /**
     * End of the challenge, the challenge is read only afterwards
     */
    @Column(name = "end_date")
    private ZonedDateTime endDate;

    public Set<User> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(Set<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Set<Result> getResults() {
        return Collections.unmodifiableSet(results);
    }

    public void setResults(Set<Result> results) {
        this.results = results;
    }

    public boolean isInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    protected void addInvitedUser(User user) {
        if (invitedUsers == null) {
            invitedUsers = new HashSet<>();
        }
        invitedUsers.add(user);
    }

    public void addResult(Result result) {
        if (results == null) {
            results = new HashSet<>();
        }
        results.add(result);
        result.setChallenge(this);
    }

    public ChallengeTemplate getChallengeTemplate() {
        return challengeTemplate;
    }

    public void setChallengeTemplate(ChallengeTemplate challengeTemplate) {
        this.challengeTemplate = challengeTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Challenge challenge = (Challenge) o;

        return canonicalName.equals(challenge.canonicalName);
    }

    @Override
    public int hashCode() {
        return canonicalName.hashCode();
    }
}
