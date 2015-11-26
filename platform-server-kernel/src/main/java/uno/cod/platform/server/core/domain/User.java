package uno.cod.platform.server.core.domain;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * A user, can be a coder, an organization employee, lecturer, or all of them
 * The role of the user is defined by the profile, teams and organizations he belongs
 */
@Entity
@Table(name = "user")
@NamedEntityGraph(name = "User.detail",
        attributeNodes = {
                @NamedAttributeNode("organizations"),
                @NamedAttributeNode("teams"),
                @NamedAttributeNode("invitedChallenges")
        })
public class User extends IdentifiableEntity implements UserDetails {
    @Column(unique = true, nullable = false, length = 255)
    @NotNull
    private String username;

    @Column(unique = true, nullable = false, length = 255)
    @Email
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    boolean enabled;

    boolean admin = false;

    /**
     * The current coding profile, represents his skills
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coder_profile")
    private CoderProfile coderProfile;

    /**
     * Organizations he belongs to, like github organizations
     */
    @OneToMany(mappedBy = "key.user")
    private Set<OrganizationMember> organizations;

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

    @OneToMany(mappedBy = "user")
    private Set<Result> results;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    @Column
    private ZonedDateTime lastLogin;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public Set<OrganizationMember> getOrganizations() {
        return Collections.unmodifiableSet(organizations);
    }

    protected void setOrganizations(Set<OrganizationMember> organizations) {
        this.organizations = organizations;
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

    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    public void addOrganizationMember(OrganizationMember member) {
        if (organizations == null) {
            organizations = new HashSet<>();
        }
        organizations.add(member);
    }

    public void addResult(Result result) {
        if (results == null) {
            results = new HashSet<>();
        }
        results.add(result);
        result.setUser(this);
    }
}
