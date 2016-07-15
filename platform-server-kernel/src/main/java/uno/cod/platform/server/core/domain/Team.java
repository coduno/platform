package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A coding team, unlike the CCC teams, those teams
 * can be used across multiple challenges and stays
 * persistent
 * can be used e.g. for a schoolclass
 */
@Entity
@Table(name = "team")
public class Team extends NamedEntity {
    @OneToMany(mappedBy = "key.team")
    private Set<TeamMember> members;

    @ManyToMany
    @JoinTable(
            name = "team_user",
            joinColumns = {@JoinColumn(name = "invited_team_id")},
            inverseJoinColumns = {@JoinColumn(name = "invited_user_id")}
    )
    private Set<User> invitedUsers;

    @OneToMany(mappedBy = "team")
    private Set<Result> results;

    private boolean enabled = true;

    public Team(UUID id, String canonicalName, String name) {
        super(id, canonicalName, name);
    }

    public Team(String canonicalName, String name) {
        super(canonicalName, name);
    }

    protected Team() {
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

    public Set<Result> getResults() {
        return results;
    }

    public void setResults(Set<Result> results) {
        this.results = results;
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

