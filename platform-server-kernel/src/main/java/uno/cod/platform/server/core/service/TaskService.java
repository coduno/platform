package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.mapper.task.TaskCreateMapper;
import uno.cod.platform.server.core.repository.TaskRepository;

import javax.transaction.Transactional;

/**
 * Created by vbalan on 11/17/2015.
 */
@Service
@Transactional
public class TaskService extends  AbstractBaseService<TaskRepository, Task> {
    private TaskCreateMapper mapper;

    @Autowired
    public TaskService(TaskRepository repository, TaskCreateMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    public Long create(TaskCreateDto dto){
        return save(mapper.map(dto)).getId();
    }
}
