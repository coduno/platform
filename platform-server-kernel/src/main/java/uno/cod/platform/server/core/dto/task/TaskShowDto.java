package uno.cod.platform.server.core.dto.task;

import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.assignment.AssignmentShowDto;

public class TaskShowDto extends AssignmentShowDto{
    public TaskShowDto(Task task){
        BeanUtils.copyProperties(task, this);
    }
}
