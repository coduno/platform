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
    Team findOneByCanonicalName(String canonicalName);

    @Query("SELECT DISTINCT team from Team team " +
            "LEFT JOIN FETCH team.members members " +
            "WHERE members.key.user.id = :userId")
    List<Team> findAllByUserId(@Param("userId") UUID userId);
}

