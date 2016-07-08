package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Team;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    Team findByCanonicalNameAndEnabledTrue(String canonicalName);

    Team findByCanonicalName(String canonicalName);

    Team findByNameAndEnabledTrue(String canonicalName);

    @Query("SELECT DISTINCT team from Team team " +
            "JOIN FETCH team.members members " +
            "JOIN FETCH team.members " +
            "WHERE team.enabled = true AND members.key.user.username = :username")
    List<Team> findAllByUsername(@Param("username") String username);
}