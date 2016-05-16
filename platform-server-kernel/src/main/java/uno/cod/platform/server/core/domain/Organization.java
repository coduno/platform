package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An organization, can be a school, company or something else
 * The concept is similar to github organizations
 */
@Entity
@Table(name = "organization",
        uniqueConstraints = {@UniqueConstraint(name = "nick", columnNames = "nick")}
)
public class Organization extends IdentifiableEntity implements CanonicalEntity {
    @JoinColumn(name = "nick", nullable = false, unique = true)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private CanonicalName nick;

    @Column(nullable = false)
    private String name;

    /**
     * all organization memberships
     */
    @OneToMany(mappedBy = "key.organization")
    private Set<OrganizationMembership> memberships;

    /**
     * challenges owned by the organization
     */
    @OneToMany(mappedBy = "organization")
    private Set<ChallengeTemplate> challengeTemplates;

    @OneToMany(mappedBy = "organization")
    private Set<Task> tasks;

    public CanonicalName getCanonicalName() {
        return nick;
    }

    public void setCanonicalName(CanonicalName nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<OrganizationMembership> getMemberships() {
        return Collections.unmodifiableSet(memberships);
    }

    protected void setMemberships(Set<OrganizationMembership> memberships) {
        this.memberships = memberships;
    }

    public Set<ChallengeTemplate> getChallengeTemplates() {
        return Collections.unmodifiableSet(challengeTemplates);
    }

    protected void setChallengeTemplates(Set<ChallengeTemplate> challengeTemplates) {
        this.challengeTemplates = challengeTemplates;
    }

    public Set<Task> getTasks() {
        return Collections.unmodifiableSet(tasks);
    }

    protected void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public void addOrganizationMembership(OrganizationMembership membership) {
        if (membership == null) {
            throw new IllegalArgumentException("organization.member.invalid");
        }
        if (memberships == null) {
            memberships = new HashSet<>();
        }
        memberships.add(membership);
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("task.invalid");
        }
        if (tasks == null) {
            tasks = new HashSet<>();
        }
        task.setOrganization(this);
        tasks.add(task);
    }

    public void addChallenge(ChallengeTemplate challengeTemplate) {
        if (challengeTemplate == null) {
            throw new IllegalArgumentException("challenge.invalid");
        }
        if (challengeTemplates == null) {
            challengeTemplates = new HashSet<>();
        }
        challengeTemplate.setOrganization(this);
        challengeTemplates.add(challengeTemplate);
    }

}
