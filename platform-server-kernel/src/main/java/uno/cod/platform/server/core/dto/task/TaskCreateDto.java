package uno.cod.platform.server.core.dto.task;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by vbalan on 11/17/2015.
 */
public class TaskCreateDto {
    @NotNull
    @Size(min = 5, max = 40)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
