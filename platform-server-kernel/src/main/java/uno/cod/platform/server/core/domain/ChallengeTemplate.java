package uno.cod.platform.server.core.domain;

import org.hibernate.validator.constraints.NotEmpty;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A challenge is a sequence of tasks, the runtime
 * does not know about this, routing the user between
 * tasks will be done by the platform
 */
@Entity
@Table(name = "challenge_template")
public class ChallengeTemplate extends Assignment implements CanonicalEntity{
    @ManyToOne
    private Endpoint endpoint;

    @ManyToOne
    private Organization organization;

    @NotNull
    @NotEmpty
    @Column(name = "canonical_name", unique = true, nullable = false)
    private String canonicalName;

    @OrderColumn(name = "task_order")
    @ManyToMany
    @JoinTable(name = "challenge_template_task",
            joinColumns = {@JoinColumn(name = "challenge_template_id")},
            inverseJoinColumns = {@JoinColumn(name = "task_id")})
    private List<Task> tasks;

    @OneToMany(mappedBy = "challengeTemplate")
    private Set<Challenge> challenges;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new CodunoIllegalArgumentException("task.invalid");
        }
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        task.addChallenge(this);
        tasks.add(task);
    }

    public Set<Challenge> getChallenges() {
        return Collections.unmodifiableSet(challenges);
    }

    public void setChallenges(Set<Challenge> challenges) {
        this.challenges = challenges;
    }

    @Override
    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
