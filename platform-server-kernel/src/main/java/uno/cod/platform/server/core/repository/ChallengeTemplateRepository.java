package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.ChallengeTemplate;

import java.util.List;

@Repository
public interface ChallengeTemplateRepository extends JpaRepository<ChallengeTemplate, Long> {

    @Query("SELECT challenge FROM ChallengeTemplate challenge " +
            "LEFT JOIN FETCH challenge.organization " +
            "WHERE challenge.id = :id")
    ChallengeTemplate findOneWithOrganization(@Param("id") Long id);

    @Query("SELECT challengeTemplate FROM ChallengeTemplate challengeTemplate " +
            "LEFT JOIN FETCH challengeTemplate.endpoint " +
            "LEFT JOIN FETCH challengeTemplate.tasks task " +
            "LEFT JOIN FETCH challengeTemplate.challenges challenge " +
            "LEFT JOIN FETCH task.endpoint " +
            "WHERE challengeTemplate.id = :id")
    ChallengeTemplate findOneWithEndpointAndTasksAndChallenges(@Param("id") Long id);

    @Query("SELECT distinct challenge FROM ChallengeTemplate challenge " +
            "JOIN FETCH challenge.organization organization " +
            "JOIN FETCH challenge.tasks tasks "+
            "WHERE organization.id = :organizationId AND challenge.tasks IS NOT EMPTY")
    List<ChallengeTemplate> findAllWithTasks(@Param("organizationId") Long organizationId);
}
