package uno.cod.platform.server.core.dto.test;

import java.util.UUID;

public class OutputTestResultDto {
    private UUID testId;
    private boolean successful;
    private byte[] stdout;
    private byte[] stderr;

    public OutputTestResultDto(UUID testId, boolean successful){
        this.testId = testId;
        this.successful = successful;
    }

    public UUID getTestId() {
        return testId;
    }

    public void setTestId(UUID testId) {
        this.testId = testId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public byte[] getStdout() {
        return stdout;
    }

    public void setStdout(byte[] stdout) {
        this.stdout = stdout;
    }

    public byte[] getStderr() {
        return stderr;
    }

    public void setStderr(byte[] stderr) {
        this.stderr = stderr;
    }
}
