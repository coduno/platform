package uno.cod.platform.server.core.dto.result;

import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.dto.task.result.TaskResultShowDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResultInfoDto {
    private UUID id;
    private List<TaskResultShowDto> taskResults;
    private ZonedDateTime started;
    private ZonedDateTime finished;

    public ResultInfoDto(Result result){
        this.id = result.getId();
        this.started = result.getStarted();
        this.finished = result.getFinished();
        if(result.getTaskResults()==null){
            return;
        }
        this.taskResults = result.getTaskResults().stream().map(TaskResultShowDto::new).collect(Collectors.toList());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<TaskResultShowDto> getTaskResults() {
        return taskResults;
    }

    public void setTaskResults(List<TaskResultShowDto> taskResults) {
        this.taskResults = taskResults;
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
}
