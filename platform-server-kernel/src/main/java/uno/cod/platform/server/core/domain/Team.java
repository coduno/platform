package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A coding team, unlike the CCC teams, those teams
 * can be used across multiple challenges and stays
 * persistent
 * can be used e.g. for a schoolclass
 */
@Entity
@Table(name = "team")
public class Team extends IdentifiableEntity implements CanonicalEntity {
    @Column(unique = true, nullable = false)
    private String name;

    @JoinColumn(name = "canonical_name", nullable = false, unique = true)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private CanonicalName canonicalName;

    @OneToMany(mappedBy = "key.team")
    private Set<TeamMember> members;

    @ManyToMany
    @JoinTable(
            name = "team_user",
            joinColumns = {@JoinColumn(name = "invited_team_id")},
            inverseJoinColumns = {@JoinColumn(name = "invited_user_id")}
    )
    private Set<User> invitedUsers;

    private boolean enabled = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CanonicalName getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(CanonicalName canonicalName) {
        this.canonicalName = canonicalName;
    }

    public Set<TeamMember> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    protected void setMembers(Set<TeamMember> members) {
        this.members = members;
    }

    public Set<User> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(Set<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected void addInvitedUser(User user) {
        if (invitedUsers == null) {
            invitedUsers = new HashSet<>();
        }
        invitedUsers.add(user);
    }

    public void addTeamMember(TeamMember member) {
        if (members == null) {
            members = new HashSet<>();
        }
        members.add(member);
    }
}

