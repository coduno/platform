package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.TeamMember;
import uno.cod.platform.server.core.domain.TeamMemberKey;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberKey> {
}
