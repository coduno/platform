package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * A submission for a (task, challenge, user). This
 * represents what the user uploaded, e.g. gave us
 * to execute.
 */
@Entity
@Table(name = "submission")
public class Submission extends IdentifiableEntity implements StoredObject {
    @ManyToOne(optional = false)
    private TaskResult taskResult;

    @OneToMany(mappedBy = "submission")
    private List<TestResult> testResults;

    /**
     * Language this submission is in. Note that as we have
     * multiple files bundled in one Submission, the individual
     * files may well use different programming languages, so we
     * might use a more sophisticated way of encoding this as soon
     * as necessary.
     */
    @ManyToOne
    private Language language;

    /**
     * The point in time we received this submission.
     */
    @Column(name = "submission_time")
    private ZonedDateTime submissionTime;

    /**
     * True if all associated tests ({@see #testResults}) succeeded.
     */
    private boolean successful;

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public ZonedDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(ZonedDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String filePath() {
        return getId() + "/";
    }
}
