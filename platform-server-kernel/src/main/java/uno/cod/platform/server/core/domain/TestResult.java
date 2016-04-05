package uno.cod.platform.server.core.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "test_result")
public class TestResult extends IdentifiableEntity {
    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false, updatable = false)
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false, updatable = false)
    private Test test;

    private boolean successful;

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
