package uno.cod.platform.server.core.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class OrganizationMembershipKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private User user;

    public OrganizationMembershipKey(User user, Organization organization) {
        this.user = user;
        this.organization = organization;
    }

    protected OrganizationMembershipKey() {
    }

    public Organization getOrganization() {
        return this.organization;
    }

    protected void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getUser() {
        return this.user;
    }

    protected void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrganizationMembershipKey that = (OrganizationMembershipKey) o;

        if (organization != null ? !organization.equals(that.organization) : that.organization != null) {
            return false;
        }
        return !(user != null ? !user.equals(that.user) : that.user != null);

    }

    @Override
    public int hashCode() {
        int result = organization != null ? organization.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
