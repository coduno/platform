package uno.cod.platform.server.core.domain;

import org.hibernate.validator.constraints.NotEmpty;
import uno.cod.platform.server.core.Named;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
abstract class NamedEntity extends CanonicalEntity implements Named<UUID> {
    @NotEmpty
    @Column(nullable = false)
    protected String name;

    protected NamedEntity(UUID id, String canonicalName, String name) {
        super(id, canonicalName);
        this.name = name;
    }

    protected NamedEntity(String canonicalName, String name) {
        this(null, canonicalName, name);
    }

    protected NamedEntity() {
        super(null, null);
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }
}
