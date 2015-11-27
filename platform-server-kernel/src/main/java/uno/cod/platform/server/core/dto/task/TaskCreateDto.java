package uno.cod.platform.server.core.dto.task;

import uno.cod.platform.server.core.dto.assignment.AssignmentCreateDto;

public class TaskCreateDto extends AssignmentCreateDto{
    private boolean isPublic = false;

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
