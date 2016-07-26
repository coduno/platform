package uno.cod.platform.server.core.domain;

import org.hibernate.annotations.GenericGenerator;
import uno.cod.platform.server.core.Identifiable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Bare bones of a JPA entity that fulfills {@link Identifiable}.
 * Superclasses may extend this type for consistency.
 */
@MappedSuperclass
public abstract class IdentifiableEntity implements Identifiable<UUID>, Serializable {
    private static final long serialVersionUID = 2L;

    @Id
    @NotNull
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uno.cod.platform.server.core.config.UseExistingOrGenerateUuidGenerator")
    @Column(columnDefinition = "BINARY(16)")
    protected UUID id;

    protected IdentifiableEntity(UUID id) {
        this.id = id;
    }

    protected IdentifiableEntity() {
    }

    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }
}