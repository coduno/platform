package uno.cod.platform.server.core.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Duration;

@MappedSuperclass
public abstract class Assignment extends IdentifiableEntity{
    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String instructions;

    @Column(nullable = false)
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
