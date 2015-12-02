package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A challenge is a sequence of tasks, the runtime
 * does not know about this, routing the user between
 * tasks will be done by the platform
 */
@Entity
@Table(name = "challenge")
public class Challenge extends Assignment {
    @ManyToOne
    private Endpoint endpoint;

    @ManyToOne
    private Organization organization;

    @ManyToMany
    private Set<User> invitedUsers;

    @OrderColumn
    @ManyToMany
    private List<Task> tasks;

    @OneToMany(mappedBy = "challenge")
    private Set<Result> results;

    /**
     * Start of the challenge, users can already be invited before
     */
    private ZonedDateTime startDate;

    /**
     * End of the challenge, the challenge is read only afterwards
     */
    private ZonedDateTime endDate;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Set<User> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(Set<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
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

    public void addResult(Result result) {
        if (results == null) {
            results = new HashSet<>();
        }
        results.add(result);
        result.setChallenge(this);
    }
}
