package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Result holds the performance of an user for a challenge.
 * It is fixed once the user has completed the Challenge or timed out.
 */
@Entity
@Table(name = "result")
public class Result extends IdentifiableEntity {
    @ManyToOne
    private User user;

    @ManyToOne
    private Challenge challenge;

    private ZonedDateTime started;

    private ZonedDateTime finished;

    @OneToMany(mappedBy = "key.result")
    private List<TaskResult> taskResults;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public ZonedDateTime getStarted() {
        return started;
    }

    public void setStarted(ZonedDateTime started) {
        this.started = started;
    }

    public ZonedDateTime getFinished() {
        return finished;
    }

    public void setFinished(ZonedDateTime finished) {
        this.finished = finished;
    }

    public List<TaskResult> getTaskResults() {
        if(taskResults == null){
            return null;
        }
        return Collections.unmodifiableList(taskResults);
    }

    public void setTaskResults(List<TaskResult> taskResults) {
        this.taskResults = taskResults;
    }
}
