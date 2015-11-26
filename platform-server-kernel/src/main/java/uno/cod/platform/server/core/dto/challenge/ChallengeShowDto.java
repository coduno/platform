package uno.cod.platform.server.core.dto.challenge;

import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.dto.endpoint.EndpointShowDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;

import java.util.List;

public class ChallengeShowDto {
    private Long id;
    private String name;
    private String description;
    private String instructions;
    private EndpointShowDto endpoint;
    private List<TaskShowDto> tasks;

    public ChallengeShowDto(Challenge challenge){
        BeanUtils.copyProperties(challenge, this);
    }

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

    public List<TaskShowDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskShowDto> tasks) {
        this.tasks = tasks;
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
}
