package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.TeamInvitation;
import uno.cod.platform.server.core.domain.TeamUserKey;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, TeamUserKey> {
    @Query("SELECT DISTINCT invitation FROM TeamInvitation invitation " +
            "WHERE invitation.key.team.enabled = true AND invitation.key.user.id = :userId")
    List<TeamInvitation> findAllByUserIdAndTeamEnabled(@Param("userId") UUID userId);

    TeamInvitation findByKey(TeamUserKey key);

    @Query("SELECT i FROM TeamInvitation i WHERE i.key.team=:team")
    List<TeamInvitation> findAllByTeam(@Param("team") Team team);

    @Modifying
    @Query("DELETE FROM TeamInvitation i WHERE i.key.team = :team")
    List<TeamInvitation> deleteAllForTeam(@Param("team") Team team);
}
