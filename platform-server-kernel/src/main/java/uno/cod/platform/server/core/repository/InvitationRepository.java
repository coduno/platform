package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Invitation;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, String> {

    @Modifying
    @Transactional
    @Query("delete from Invitation i where i.expire < :now")
    void deleteExpiredTokens(@Param("now") ZonedDateTime now);

    @Query("SELECT invitation FROM Invitation invitation " +
            "LEFT JOIN FETCH invitation.challenge challenge " +
            "WHERE challenge.id = :challenge")
    List<Invitation> findAllByChallenge(@Param("challenge") UUID challenge);

    @Query("SELECT invitation FROM Invitation invitation " +
            "LEFT JOIN FETCH invitation.challenge challenge " +
            "WHERE challenge.canonicalName = :challenge")
    Set<Invitation> findAllByChallengeCanonicalName(@Param("challenge") String challenge);
}
