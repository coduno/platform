package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uno.cod.platform.server.core.domain.Challenge;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {

    @Query("SELECT challenge FROM Challenge challenge " +
            "JOIN FETCH challenge.challengeTemplate challengeTemplate " +
            "LEFT JOIN FETCH challengeTemplate.organization organization " +
            "WHERE challengeTemplate.id=:templateId AND challenge.startDate=:startDate AND organization.id=:organizationId")
    Challenge findOneByTemplateAndStartDateAndOrganization(@Param("templateId") UUID challengeTemplate,
                                                           @Param("startDate") ZonedDateTime startDate,
                                                           @Param("organizationId") UUID organizationId);

    @Query("SELECT challenge FROM Challenge challenge " +
            "JOIN FETCH challenge.challengeTemplate template " +
            "WHERE challenge.id=:id")
    Challenge findOneWithTemplate(@Param("id") UUID id);

    @Query("SELECT challenge FROM Challenge challenge " +
            "JOIN FETCH challenge.invitedUsers " +
            "WHERE challenge.id=:id")
    Challenge findOneWithUsers(@Param("id") UUID id);

    @Query("SELECT challenge FROM Challenge challenge " +
            "JOIN FETCH challenge.invitedUsers users " +
            "WHERE users.id = :user")
    List<Challenge> findAllByInvitedUser(@Param("user") UUID id);

    @Query("SELECT challenge FROM Challenge challenge " +
            "LEFT JOIN FETCH challenge.challengeTemplate challengeTemplate " +
            "LEFT JOIN FETCH challengeTemplate.organization organization " +
            "WHERE challenge.inviteOnly= :inviteOnly AND challenge.endDate > :endDate")
    List<Challenge> findAllByInviteOnlyAndEndDateAfter(@Param("inviteOnly") Boolean inviteOnly, @Param("endDate")ZonedDateTime endDate);

    @Query("SELECT challenge FROM Challenge challenge " +
            "LEFT JOIN FETCH challenge.challengeTemplate challengeTemplate " +
            "LEFT JOIN FETCH challengeTemplate.organization organization " +
            "WHERE organization.id = :organization")
    List<Challenge> findAllByOrganization(@Param("organization") UUID organization);
}
