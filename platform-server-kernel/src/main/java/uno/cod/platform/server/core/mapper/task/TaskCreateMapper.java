package uno.cod.platform.server.core.mapper.task;

import org.springframework.stereotype.Component;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.mapper.Mapper;

/**
 * Created by vbalan on 11/17/2015.
 */
@Component
public class TaskCreateMapper implements Mapper<TaskCreateDto, Task> {
    @Override
    public Task map(TaskCreateDto dto) {
        Task task = new Task();
        task.setName(dto.getName());
        return task;
    }

}
