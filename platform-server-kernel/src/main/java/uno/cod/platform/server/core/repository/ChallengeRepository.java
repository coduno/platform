package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Challenge;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    Challenge findOneByCanonicalName(String canonicalName);

    @Query("SELECT challenge FROM Challenge challenge " +
            "JOIN FETCH challenge.challengeTemplate template " +
            "WHERE challenge.canonicalName=:canonicalName")
    Challenge findOneByCanonicalNameWithTemplate(@Param("canonicalName") String canonicalName);

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

    @Query("SELECT DISTINCT challenge FROM Challenge challenge " +
            "LEFT JOIN FETCH challenge.invitedUsers " +
            "LEFT JOIN FETCH challenge.participations " +
            "WHERE challenge.canonicalName = :canonicalName")
    Challenge findOneByCanonicalNameWithInvitedUsersAndRegisteredUsers(@Param("canonicalName") String canonicalName);

    @Query("SELECT DISTINCT challenge FROM Challenge challenge " +
            "LEFT JOIN FETCH challenge.challengeTemplate template " +
            "LEFT JOIN FETCH template.organization " +
            "LEFT JOIN FETCH challenge.invitedUsers invitedUser " +
            "LEFT JOIN FETCH challenge.participations " +
            "WHERE (challenge.endDate IS NULL OR challenge.endDate > current_date) " +
            "AND (challenge.inviteOnly = false OR invitedUser.id = :user)")
    List<Challenge> findAllValidWithOrganizationAndInvitedUsersAndRegisteredUsers(@Param("user") UUID user);

    @Query("SELECT challenge FROM Challenge challenge " +
            "LEFT JOIN FETCH challenge.challengeTemplate challengeTemplate " +
            "LEFT JOIN FETCH challengeTemplate.organization organization " +
            "WHERE organization.id = :organization")
    List<Challenge> findAllByOrganization(@Param("organization") UUID organization);

}
