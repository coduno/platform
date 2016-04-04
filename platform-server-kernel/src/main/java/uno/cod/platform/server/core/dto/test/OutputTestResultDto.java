package uno.cod.platform.server.core.dto.test;

import java.util.UUID;

public class OutputTestResultDto {
    private UUID testId;
    private boolean green;
    private byte[] stdout;
    private byte[] stderr;

    public OutputTestResultDto(UUID testId, boolean green){
        this.testId = testId;
        this.green = green;
    }

    public UUID getTestId() {
        return testId;
    }

    public void setTestId(UUID testId) {
        this.testId = testId;
    }

    public boolean isGreen() {
        return green;
    }

    public void setGreen(boolean green) {
        this.green = green;
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
