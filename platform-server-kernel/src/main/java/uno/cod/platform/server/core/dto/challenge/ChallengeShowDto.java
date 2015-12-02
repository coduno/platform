package uno.cod.platform.server.core.dto.challenge;

import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.dto.assignment.AssignmentShowDto;
import uno.cod.platform.server.core.dto.endpoint.EndpointShowDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;

import java.util.List;

public class ChallengeShowDto extends AssignmentShowDto{
    private List<TaskShowDto> tasks;

    public ChallengeShowDto(Challenge challenge){
        BeanUtils.copyProperties(challenge, this);
    }

    public List<TaskShowDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskShowDto> tasks) {
        this.tasks = tasks;
    }
}
