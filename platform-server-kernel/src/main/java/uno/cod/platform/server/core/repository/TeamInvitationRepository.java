package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uno.cod.platform.server.core.domain.TeamInvitation;
import uno.cod.platform.server.core.domain.TeamUserKey;

import java.util.List;
import java.util.UUID;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, TeamUserKey> {
    @Query("SELECT DISTINCT invitation FROM TeamInvitation invitation " +
            "WHERE invitation.key.user.id = :userId")
    List<TeamInvitation> findAllByUserId(@Param("userId") UUID userId);
}
