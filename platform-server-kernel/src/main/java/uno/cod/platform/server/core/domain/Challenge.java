package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "challenge")
public class Challenge extends NamedEntity {
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

    /**
     * If it's null or empty, the challenge is online only.
     */
    @OneToMany(mappedBy = "key.challenge")
    private Set<LocationDetail> locationDetails;

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

    @OneToMany(mappedBy = "key.challenge")
    private Set<Participation> participations;

    public Challenge(UUID id, String canonicalName, String name) {
        super(id, canonicalName, name);
    }

    public Challenge(String canonicalName, String name) {
        super(canonicalName, name);
    }

    protected Challenge() {
    }

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

    public Set<Participation> getParticipations() {
        if (participations == null) {
            return null;
        }
        return Collections.unmodifiableSet(participations);
    }

    protected void setParticipations(Set<Participation> participations) {
        this.participations = participations;
    }

    protected void addInvitedUser(User user) {
        if (invitedUsers == null) {
            invitedUsers = new HashSet<>();
        }
        invitedUsers.add(user);
    }

    protected void removeInvitedUser(User user) {
        if (invitedUsers == null) {
            return;
        }
        invitedUsers.remove(user);
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

    public void setName(String name) {
        this.name = name;
    }

    public Set<LocationDetail> getLocationDetails() {
        if (locationDetails == null) {
            locationDetails = new HashSet<>();
        }
        return Collections.unmodifiableSet(locationDetails);
    }

    private void setLocationDetails(Set<LocationDetail> locationDetails) {
        this.locationDetails = locationDetails;
    }

    @Override
    public void setCanonicalName(String canonicalName) {
        setCanonicalName(canonicalName);
    }
}
