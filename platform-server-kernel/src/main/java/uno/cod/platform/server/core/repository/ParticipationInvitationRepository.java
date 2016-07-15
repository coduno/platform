package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.ParticipationInvitation;
import uno.cod.platform.server.core.domain.ParticipationKey;

import java.util.UUID;

@Repository
public interface ParticipationInvitationRepository extends JpaRepository<ParticipationInvitation, ParticipationKey> {
    @Query("SELECT invitation FROM ParticipationInvitation invitation " +
            "LEFT JOIN FETCH invitation.emails invited " +
            "WHERE invitation.key.challenge.id = :challenge AND invited = :email")
    ParticipationInvitation findOneByChallengeAndEmailsContaining(@Param("challenge") UUID challenge, @Param("email") String email);
}
