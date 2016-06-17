package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.PasswordResetToken;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    @Modifying
    @Transactional
    @Query("delete from PasswordResetToken p where p.expire < :now")
    void deleteExpiredTokens(@Param("now") ZonedDateTime now);
}
