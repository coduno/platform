package uno.cod.platform.server.core.dto.task;

import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.Task;

/**
 * Created by vbalan on 11/17/2015.
 */
public class TaskShowDto {
    private String name;

    public TaskShowDto(Task task) {
        BeanUtils.copyProperties(task, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
