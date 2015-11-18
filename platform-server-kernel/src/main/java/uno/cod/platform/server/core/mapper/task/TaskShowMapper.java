package uno.cod.platform.server.core.mapper.task;

import org.springframework.stereotype.Component;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.mapper.CollectionMapper;

/**
 * Created by vbalan on 11/18/2015.
 */
@Component
public class TaskShowMapper extends CollectionMapper<Task,TaskShowDto> {
    @Override
    public TaskShowDto map(Task task) {
        return new TaskShowDto(task);
    }
}
