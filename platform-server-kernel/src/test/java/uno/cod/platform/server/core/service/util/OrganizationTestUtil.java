package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Organization;

import java.util.UUID;

public class OrganizationTestUtil {
    public static Organization getOrganization(){
        return getOrganization(UUID.randomUUID());
    }

    public static Organization getOrganization(UUID id){
        Organization organization = new Organization();
        organization.setId(id);
        organization.setName("Coduno");
        organization.setNick("coduno");
        return organization;
    }
}
