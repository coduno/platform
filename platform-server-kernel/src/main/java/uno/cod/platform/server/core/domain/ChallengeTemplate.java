package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.*;

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
    @ManyToMany
    private List<Task> tasks;

    @OneToMany(mappedBy = "challengeTemplate")
    private List<Challenge> challenges;

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

    public List<Challenge> getChallenges() {
        return Collections.unmodifiableList(challenges);
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }
}
