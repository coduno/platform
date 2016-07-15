package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uno.cod.platform.server.core.domain.ActivationToken;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, UUID> {
    @Modifying
    @Transactional
    @Query("delete from ActivationToken i where i.expire < :now")
    void deleteExpiredTokens(@Param("now") ZonedDateTime now);

    @Query("SELECT invitation FROM ActivationToken invitation " +
            "LEFT JOIN FETCH invitation.challenge challenge " +
            "WHERE challenge.id = :challenge")
    List<ActivationToken> findAllByChallenge(@Param("challenge") UUID challenge);

    @Query("SELECT invitation FROM ActivationToken invitation " +
            "LEFT JOIN FETCH invitation.challenge challenge " +
            "WHERE challenge.canonicalName = :challenge")
    Set<ActivationToken> findAllByChallengeCanonicalName(@Param("challenge") String challenge);

    ActivationToken findByEmail(String email);
}
