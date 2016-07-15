package uno.cod.platform.server.core.domain;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import java.time.Duration;
import java.util.UUID;

/**
 * Assignment conveys the specification of what a user
 * must do in order to fulfill a task or challenge.
 */
@MappedSuperclass
abstract class Assignment extends NamedEntity {
    @Column(nullable = false)
    @Lob
    private String description;

    @Column(nullable = false)
    @Lob
    private String instructions;

    @Column(nullable = false)
    private Duration duration;

    Assignment(UUID id, String canonicalName, String name) {
        super(id, canonicalName, name);
    }

    Assignment(String canonicalName, String name) {
        super(canonicalName, name);
    }

    protected Assignment() {
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
