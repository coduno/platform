package uno.cod.platform.server.core.mapper;

import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskShowDto;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {
    public static TaskShowDto map(Task task) {
        TaskShowDto dto = new TaskShowDto(task);
        if (task.getEndpoint() != null) {
            dto.setEndpoint(EndpointMapper.map(task.getEndpoint()));
        }
        if (task.getTemplates() != null) {
            dto.setTemplates(task.getTemplates());
        }
        return dto;
    }

    public static List<TaskShowDto> map(List<Task> tasks) {
        return tasks.stream().map(TaskMapper::map).collect(Collectors.toList());
    }
}
