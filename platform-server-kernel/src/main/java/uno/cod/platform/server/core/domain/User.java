package uno.cod.platform.server.core.domain;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUserDetails;
import uno.cod.platform.server.core.Named;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * A user, can be a coder, an organization employee, lecturer, or all of them
 * The role of the user is defined by the profile, teams and organizationMemberships he belongs
 */
@Entity
@Table(name = "user")
@NamedEntityGraph(name = "User.detail",
        attributeNodes = {
                @NamedAttributeNode("organizationMemberships"),
                @NamedAttributeNode("teams"),
                @NamedAttributeNode("invitedChallenges")
        }
)
public class User extends CanonicalEntity implements Named<UUID>, SocialUserDetails {
    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<AccessToken> accessTokens;

    private boolean enabled;

    private boolean admin;

    public User(String canonicalName, String email, String password, String firstName, String lastName) {
        super(canonicalName);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.enabled = true;
        this.admin = false;
    }

    public User(UUID id) {
        super(id);
    }

    public User() {
    }

    /**
     * The current coding profile, represents his skills
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coder_profile")
    private CoderProfile coderProfile;

    /**
     * Organizations he belongs to, like github organizationMemberships
     */
    @OneToMany(mappedBy = "key.user")
    private Set<OrganizationMembership> organizationMemberships;

    /**
     * Teams he belongs to, can be used across multiple
     * challenges
     */
    @OneToMany(mappedBy = "key.user")
    private Set<TeamMember> teams;

    /**
     * Private (company) challenges he is invited
     */
    @ManyToMany(mappedBy = "invitedUsers")
    private Set<Challenge> invitedChallenges;

    @ManyToMany(mappedBy = "invitedUsers")
    private Set<Team> invitedTeams;

    @OneToMany(mappedBy = "user")
    private Set<Result> results;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

    @OneToMany(mappedBy = "key.user")
    private Set<Participation> participations;

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<AccessToken> getAccessTokens() {
        return Collections.unmodifiableSet(accessTokens);
    }

    protected void setAccessTokens(Set<AccessToken> accessTokens) {
        this.accessTokens = accessTokens;
    }

    public Set<OrganizationMembership> getOrganizationMemberships() {
        if (this.organizationMemberships == null) {
            return null;
        }
        return Collections.unmodifiableSet(organizationMemberships);
    }

    protected void setOrganizationMemberships(Set<OrganizationMembership> organizationMemberships) {
        this.organizationMemberships = organizationMemberships;
    }

    public Set<TeamMember> getTeams() {
        return Collections.unmodifiableSet(teams);
    }

    protected void setTeams(Set<TeamMember> teams) {
        this.teams = teams;
    }

    public Set<Challenge> getInvitedChallenges() {
        return Collections.unmodifiableSet(invitedChallenges);
    }

    protected void setInvitedChallenges(Set<Challenge> invitedChallenges) {
        this.invitedChallenges = invitedChallenges;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    private void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(ZonedDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public CoderProfile getCoderProfile() {
        return coderProfile;
    }

    public void setCoderProfile(CoderProfile coderProfile) {
        this.coderProfile = coderProfile;
    }

    public Set<Result> getResults() {
        return Collections.unmodifiableSet(results);
    }

    protected void setResults(Set<Result> results) {
        this.results = results;
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

    public void addOrganizationMembership(OrganizationMembership membership) {
        if (organizationMemberships == null) {
            organizationMemberships = new HashSet<>();
        }
        organizationMemberships.add(membership);
    }

    public void addResult(Result result) {
        if (results == null) {
            results = new HashSet<>();
        }
        results.add(result);
        result.setUser(this);
    }

    public void addInvitedChallenge(Challenge challenge) {
        if (invitedChallenges == null) {
            invitedChallenges = new HashSet<>();
        }
        challenge.addInvitedUser(this);
        invitedChallenges.add(challenge);
    }

    public void removeInvitedChallenge(Challenge challenge) {
        if (invitedChallenges == null) {
            return;
        }
        challenge.removeInvitedUser(this);
        invitedChallenges.remove(challenge);
    }

    public void addInvitedTeam(Team team) {
        if (invitedTeams == null) {
            invitedTeams = new HashSet<>();
        }
        team.addInvitedUser(this);
        invitedTeams.add(team);
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> ga = new ArrayList<>();
        if (isAdmin()) {
            ga.add(() -> "ROLE_ADMIN");
        }
        return ga;
    }

    @Override
    @Transient
    public String getUserId() {
        return getId().toString();
    }

    @Override
    @Transient
    public String getUsername() {
        return getCanonicalName();
    }

    @Transient
    public void setUsername(String username) {
        setCanonicalName(username);
    }

    /**
     * Makes best efforts to construct a human readable name for {@code this}.
     * @return If {@link #firstName} and {@link #lastName} are set, it will return a
     * concatenation (also regarded as the "full name"). If only {@link #firstName}
     * is set, it will return that. As a fallback {@link #canonicalName} is
     * returned.
     */
    @Override
    @Transient
    public String getName() {
        if (firstName != null) {
            if (lastName != null) {
                return firstName + " " + lastName;
            }
            return firstName;
        }
        return canonicalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (email == null) {
            return user.email == null;
        }

        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}