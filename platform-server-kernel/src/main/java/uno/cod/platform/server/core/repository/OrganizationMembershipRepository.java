package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.OrganizationMembership;
import uno.cod.platform.server.core.domain.OrganizationMembershipKey;

@Repository
public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, OrganizationMembershipKey> {
}

