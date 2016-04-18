package uno.cod.platform.server.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
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

    @OneToMany(mappedBy = "key.team")
    private Set<TeamMember> members;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TeamMember> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    protected void setMembers(Set<TeamMember> members) {
        this.members = members;
    }

    @Override
    public String getCanonicalName() {
        return name;
    }
}

