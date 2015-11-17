package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.mapper.task.TaskShowMapper;
import uno.cod.platform.server.core.repository.TaskRepository;

/**
 * Created by vbalan on 11/18/2015.
 */
@Service
public class TaskLoadingService extends AbstractBaseLoadingService<TaskRepository, Task, TaskShowDto> {
    private TaskService taskService;
    private TaskShowMapper taskMapper;

    @Autowired
    public TaskLoadingService(TaskService taskService, TaskShowMapper taskMapper) {
        super(taskService, taskMapper);
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }
}
