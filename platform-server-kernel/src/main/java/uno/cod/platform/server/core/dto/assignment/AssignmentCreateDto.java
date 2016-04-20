package uno.cod.platform.server.core.dto.assignment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.UUID;

public abstract class AssignmentCreateDto {
    @NotNull
    @Size(min = 5, max = 20)
    private String name;

    @NotNull
    @Size(min = 5)
    private String description;

    @NotNull
    @Size(min = 5)
    private String instructions;

    @NotNull
    private UUID endpointId;

    @NotNull
    private Duration duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public UUID getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(UUID endpointId) {
        this.endpointId = endpointId;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
