package uno.cod.platform.server.core.domain;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import java.time.Duration;

/**
 * Assignment conveys the specification of what a user
 * must do in order to fulfill a task or challenge.
 */
@MappedSuperclass
public abstract class Assignment extends IdentifiableEntity{
    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    @Lob
    private String description;

    @Column(nullable = false)
    @Lob
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
