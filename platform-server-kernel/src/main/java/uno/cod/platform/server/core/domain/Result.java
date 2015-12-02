package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "result")
public class Result extends IdentifiableEntity{
    @ManyToOne
    private User user;

    @ManyToOne
    private Challenge challenge;

    @OrderColumn
    @ElementCollection
    private List<ZonedDateTime> startTimes;

    @Column
    private ZonedDateTime started;

    @Column
    private ZonedDateTime finished;

    @OneToMany(mappedBy = "result")
    private Set<Submission> submissions;

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

    public List<ZonedDateTime> getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(List<ZonedDateTime> startTimes) {
        this.startTimes = startTimes;
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

    public Set<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }

    public void addSubmission(Submission submission){
        if(submissions == null){
            submissions = new HashSet<>();
        }
        submissions.add(submission);
        submission.setResult(this);
    }
}
