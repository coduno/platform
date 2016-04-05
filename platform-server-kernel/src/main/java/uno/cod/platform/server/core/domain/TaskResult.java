package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "task_result")
@AssociationOverrides({
        @AssociationOverride(name = "key.task", joinColumns = {@JoinColumn(name = "task_id")}),
        @AssociationOverride(name = "key.result", joinColumns = {@JoinColumn(name = "result_id")})
})
public class TaskResult {
    @EmbeddedId
    private TaskResultKey key = new TaskResultKey();

    @Column(name = "start_time")
    private ZonedDateTime startTime;
    @Column(name = "end_time")
    private ZonedDateTime endTime;

    private boolean successful;

    @OneToMany(mappedBy = "taskResult")
    private Set<Submission> submissions;

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Set<Submission> getSubmissions() {
        return Collections.unmodifiableSet(submissions);
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }

    public void addSubmission(Submission submission) {
        if (submissions == null) {
            submissions = new HashSet<>();
        }
        submissions.add(submission);
        submission.setTaskResult(this);
    }

    public TaskResultKey getKey() {
        return key;
    }

    public void setKey(TaskResultKey key) {
        this.key = key;
    }
}
