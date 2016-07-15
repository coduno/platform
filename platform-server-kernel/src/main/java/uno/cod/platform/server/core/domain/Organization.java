package uno.cod.platform.server.core.domain;

import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * An organization, can be a school, company or something else
 * The concept is similar to github organizations
 */
@Entity
@Table(name = "organization",
        uniqueConstraints = {@UniqueConstraint(name = "canonical_name", columnNames = "canonical_name")}
)
public class Organization extends NamedEntity {
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

    public Organization(UUID id, String canonicalName, String name) {
        super(id, canonicalName, name);
    }

    public Organization(String canonicalName, String name) {
        super(canonicalName, name);
    }

    public Organization() {
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
            throw new CodunoIllegalArgumentException("organization.member.invalid");
        }
        if (memberships == null) {
            memberships = new HashSet<>();
        }
        memberships.add(membership);
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new CodunoIllegalArgumentException("task.invalid");
        }
        if (tasks == null) {
            tasks = new HashSet<>();
        }
        task.setOrganization(this);
        tasks.add(task);
    }

    public void addChallenge(ChallengeTemplate challengeTemplate) {
        if (challengeTemplate == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }
        if (challengeTemplates == null) {
            challengeTemplates = new HashSet<>();
        }
        challengeTemplate.setOrganization(this);
        challengeTemplates.add(challengeTemplate);
    }
}
