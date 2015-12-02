package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Challenge;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("SELECT challenge FROM Challenge challenge " +
            "LEFT JOIN FETCH challenge.organization " +
            "WHERE challenge.id = :id")
    Challenge findOneWithOrganization(@Param("id") Long id);

    @Query("SELECT challenge FROM Challenge challenge " +
            "LEFT JOIN FETCH challenge.endpoint " +
            "LEFT JOIN FETCH challenge.tasks task " +
            "LEFT JOIN FETCH task.endpoint " +
            "WHERE challenge.id = :id")
    Challenge findOneWithEndpointAndTasks(@Param("id") Long id);

    @Query("SELECT challenge FROM Challenge challenge " +
            "LEFT JOIN FETCH challenge.tasks " +
            "LEFT JOIN FETCH challenge.organization organization " +
            "WHERE organization.id = :organizationId")
    List<Challenge> findAllWithTasks(@Param("organizationId") Long organizationId);
}

