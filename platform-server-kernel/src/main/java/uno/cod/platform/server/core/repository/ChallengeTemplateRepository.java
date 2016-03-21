package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.ChallengeTemplate;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeTemplateRepository extends JpaRepository<ChallengeTemplate, UUID> {
    @Query("SELECT template FROM ChallengeTemplate template " +
            "WHERE template.nick = :nick")
    ChallengeTemplate findOneByNick(@Param("nick") String nick);

    @Query("SELECT challenge FROM ChallengeTemplate challenge " +
            "LEFT JOIN FETCH challenge.organization " +
            "WHERE challenge.id = :id")
    ChallengeTemplate findOneWithOrganization(@Param("id") UUID id);

    @Query("SELECT challengeTemplate FROM ChallengeTemplate challengeTemplate " +
            "LEFT JOIN FETCH challengeTemplate.endpoint " +
            "LEFT JOIN FETCH challengeTemplate.tasks task " +
            "LEFT JOIN FETCH challengeTemplate.challenges challenge " +
            "LEFT JOIN FETCH task.endpoint " +
            "WHERE challengeTemplate.id = :id")
    ChallengeTemplate findOneWithEndpointAndTasksAndChallenges(@Param("id") UUID id);

    @Query("SELECT distinct challenge FROM ChallengeTemplate challenge " +
            "JOIN FETCH challenge.organization organization " +
            "JOIN FETCH challenge.tasks tasks "+
            "WHERE organization.id = :organizationId AND challenge.tasks IS NOT EMPTY")
    List<ChallengeTemplate> findAllWithTasks(@Param("organizationId") UUID organizationId);
}

