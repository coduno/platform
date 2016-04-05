package uno.cod.platform.server.core.dto.task.result;

import uno.cod.platform.server.core.domain.TaskResult;
import uno.cod.platform.server.core.dto.task.TaskShowDto;

import java.time.ZonedDateTime;

public class TaskResultShowDto {
    private TaskShowDto task;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Boolean successful;
    private Integer numberOfSubmissions;

    public TaskResultShowDto(TaskResult taskResult) {
        this.task = new TaskShowDto(taskResult.getKey().getTask());
        this.startTime = taskResult.getStartTime();
        this.endTime = taskResult.getEndTime();
        this.successful = taskResult.isSuccessful();
        this.numberOfSubmissions = taskResult.getSubmissions() != null ? taskResult.getSubmissions().size() : 0;
    }

    public TaskShowDto getTask() {
        return task;
    }

    public void setTask(TaskShowDto task) {
        this.task = task;
    }

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

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public Integer getNumberOfSubmissions() {
        return numberOfSubmissions;
    }

    public void setNumberOfSubmissions(Integer numberOfSubmissions) {
        this.numberOfSubmissions = numberOfSubmissions;
    }
}
