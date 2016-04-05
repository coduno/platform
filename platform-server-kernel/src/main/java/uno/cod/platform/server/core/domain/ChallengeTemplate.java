package uno.cod.platform.server.core.domain;

import javax.persistence.*;
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
public class ChallengeTemplate extends Assignment {
    @ManyToOne
    private Endpoint endpoint;

    @ManyToOne
    private Organization organization;

    @OrderColumn
    @ManyToMany(mappedBy = "challengeTemplates")
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
            throw new IllegalArgumentException("task.invalid");
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
}
