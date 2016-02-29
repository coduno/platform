package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uno.cod.platform.server.core.domain.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {

    @Query("SELECT result FROM Result result " +
            "LEFT JOIN FETCH result.challenge challenge " +
            "LEFT JOIN FETCH challenge.tasks " +
            "WHERE result.id = :id")
    Result findOneWithChallenge(@Param("id") Long id);

    @Query("SELECT result FROM Result result " +
            "LEFT JOIN FETCH result.challenge challenge " +
            "LEFT JOIN FETCH result.user user " +
            "WHERE user.id = :user AND challenge.id = :challenge")
    Result findOneByUserAndChallenge(@Param("user") Long userId, @Param("challenge") Long challengeId);
}
