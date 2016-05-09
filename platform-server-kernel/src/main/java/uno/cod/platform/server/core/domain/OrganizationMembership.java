package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "organization_membership")
@AssociationOverrides({
        @AssociationOverride(name = "key.user", joinColumns = {@JoinColumn(name = "user_id")}),
        @AssociationOverride(name = "key.organization", joinColumns = {@JoinColumn(name = "organization_id")})
})
public class OrganizationMembership implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OrganizationMembershipKey key = new OrganizationMembershipKey();

    @Column(nullable = false, updatable = false)
    private Date created = new Date();

    private boolean admin;

    public OrganizationMembership() {
    }

    public OrganizationMembershipKey getKey() {
        return this.key;
    }

    public void setKey(OrganizationMembershipKey key) {
        this.key = key;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
