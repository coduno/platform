package uno.cod.platform.server.core.domain;

import uno.cod.platform.server.core.Canonical;
import uno.cod.platform.server.core.util.constraints.CanonicalName;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Bare bones of a JPA entity that fulfills {@link Canonical}.
 */
@MappedSuperclass
public abstract class CanonicalEntity extends IdentifiableEntity implements Canonical<UUID> {
    @CanonicalName
    @Column(name = "canonical_name", nullable = false, unique = true)
    protected String canonicalName;

    protected CanonicalEntity(UUID id, String canonicalName) {
        super(id);
        this.canonicalName = canonicalName;
    }

    protected CanonicalEntity(String canonicalName) {
        this(null, canonicalName);
    }

    protected CanonicalEntity(UUID id) {
        super(id);
    }

    protected CanonicalEntity() {
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    protected void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass().isAssignableFrom(getClass())) {
            return false;
        }

        CanonicalEntity other = (CanonicalEntity) o;
        return canonicalName.equals(other.canonicalName) && ((id == null && other.id == null) || id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return (canonicalName != null ? canonicalName.hashCode() : 0) + 31 * (id != null ? id.hashCode() : 0);
    }
}