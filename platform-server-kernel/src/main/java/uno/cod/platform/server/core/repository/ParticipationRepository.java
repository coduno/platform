package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Participation;

import java.util.UUID;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, UUID> {
    @Query("SELECT participation FROM Participation participation " +
            "LEFT JOIN FETCH participation.challenge challenge " +
            "LEFT JOIN FETCH participation.team team " +
            "LEFT JOIN FETCH team.members teamMember " +
            "LEFT JOIN FETCH participation.user user " +
            "WHERE challenge.id = :challenge AND (teamMember.key.user.id = :user OR user.id = :user)")
    Participation findOneByUserAndChallenge(@Param("user") UUID user, @Param("challenge") UUID challenge);
}
