package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * A submission for a (task, challenge, user)
 */
@Entity
@Table(name = "submission")
public class Submission extends IdentifiableEntity implements StoredObject {
    @ManyToOne(optional = false)
    private TaskResult taskResult;

    @OneToMany(mappedBy = "submission")
    private List<TestResult> testResults;

    @ManyToOne
    private Language language;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "submission_time")
    private ZonedDateTime submissionTime;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Override
    public String filePath() {
        return getId() + "/" + fileName;
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
}
