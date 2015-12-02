package uno.cod.platform.server.core.dto.challenge;

import uno.cod.platform.server.core.dto.assignment.AssignmentCreateDto;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ChallengeCreateDto extends AssignmentCreateDto{
    @NotNull
    private List<Long> tasks;

    public List<Long> getTasks() {
        return tasks;
    }

    public void setTasks(List<Long> tasks) {
        this.tasks = tasks;
    }
}
