package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * A submission for a (task, challenge, user)
 */
@Entity
@Table(name = "submission")
public class Submission extends IdentifiableEntity implements StoredObject{
    @ManyToOne(optional = false)
    private TaskResult taskResult;

    @OneToMany(mappedBy = "submission")
    private List<TestResult> testResults;

    private String fileName;

    private ZonedDateTime submissionTime;

    private boolean green;

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

    public boolean isGreen() {
        return green;
    }

    public void setGreen(boolean green) {
        this.green = green;
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
}
