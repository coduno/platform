package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.domain.OrganizationMembership;
import uno.cod.platform.server.core.domain.OrganizationMembershipKey;
import uno.cod.platform.server.core.domain.User;

public class OrganizationMembershipTestUtil {
    public static OrganizationMembership getOrganizationMembership(User user, Organization organization, boolean admin) {
        OrganizationMembership membership = new OrganizationMembership();
        OrganizationMembershipKey key = new OrganizationMembershipKey();
        key.setUser(user);
        key.setOrganization(organization);

        membership.setKey(key);
        membership.setAdmin(admin);

        return membership;
    }
}
