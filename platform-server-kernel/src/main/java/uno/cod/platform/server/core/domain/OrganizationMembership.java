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

    public OrganizationMembership(OrganizationMembershipKey key, boolean admin) {
        this.key = key;
        this.admin = admin;
    }

    public OrganizationMembership(OrganizationMembershipKey key) {
        this(key, false);
    }

    public OrganizationMembership(User user, Organization organization, boolean admin) {
        this(new OrganizationMembershipKey(user, organization), admin);
    }

    public OrganizationMembership(User user, Organization organization) {
        this(user, organization, false);
    }

    protected OrganizationMembership() {
    }

    public OrganizationMembershipKey getKey() {
        return this.key;
    }

    protected void setKey(OrganizationMembershipKey key) {
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

    protected void setCreated(Date created) {
        this.created = created;
    }
}
