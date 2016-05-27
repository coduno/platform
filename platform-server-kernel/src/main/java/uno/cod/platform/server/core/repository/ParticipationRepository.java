package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Participation;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, UUID> {
    @Query("SELECT participation FROM Participation      participation " +
            "LEFT JOIN FETCH participation.key.challenge challenge " +
            "LEFT JOIN FETCH participation.key.user      user " +
            "LEFT JOIN FETCH participation.team          team " +
            "LEFT JOIN FETCH team.members                teamMember " +
            "WHERE participation.key.user.id      = :user " +
            "AND   participation.key.challenge.id = :challenge")
    Participation findOneByUserAndChallenge(@Param("user") UUID user, @Param("challenge") UUID challenge);

    @Query("SELECT participation FROM Participation      participation " +
            "LEFT JOIN FETCH participation.key.challenge challenge " +
            "LEFT JOIN FETCH participation.key.user      user " +
            "LEFT JOIN FETCH participation.team          team " +
            "LEFT JOIN FETCH team.members                teamMember " +
            "WHERE  participation.key.challenge.canonicalName = :challenge " +
            "ORDER BY participation.created")
    Set<Participation> findAllByChallengeCanonicalName(@Param("challenge") String challenge);
}
