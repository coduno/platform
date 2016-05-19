package uno.cod.platform.server.core.dto.challenge.template;

import uno.cod.platform.server.core.dto.assignment.AssignmentCreateDto;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class ChallengeTemplateCreateDto extends AssignmentCreateDto {
    private UUID organizationId;
    @NotNull
    private List<UUID> tasks;

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public List<UUID> getTasks() {
        return tasks;
    }

    public void setTasks(List<UUID> tasks) {
        this.tasks = tasks;
    }
}
