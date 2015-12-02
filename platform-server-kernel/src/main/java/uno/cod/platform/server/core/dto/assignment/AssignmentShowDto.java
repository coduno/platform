package uno.cod.platform.server.core.dto.assignment;

import uno.cod.platform.server.core.dto.endpoint.EndpointShowDto;

import java.time.Duration;

public abstract class AssignmentShowDto {
    private Long id;
    private String name;
    private String description;
    private String instructions;
    private EndpointShowDto endpoint;
    private Duration duration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public EndpointShowDto getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointShowDto endpoint) {
        this.endpoint = endpoint;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
