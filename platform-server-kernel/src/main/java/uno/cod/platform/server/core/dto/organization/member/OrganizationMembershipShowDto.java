package uno.cod.platform.server.core.dto.organization.member;

import uno.cod.platform.server.core.domain.OrganizationMembership;
import uno.cod.platform.server.core.dto.organization.OrganizationShowDto;

public class OrganizationMembershipShowDto extends OrganizationShowDto {
    private boolean admin;

    public OrganizationMembershipShowDto(OrganizationMembership membership) {
        super(membership.getKey().getOrganization());
        this.admin = membership.isAdmin();
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
