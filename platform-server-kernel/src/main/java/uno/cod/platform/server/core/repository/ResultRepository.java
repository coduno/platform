package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uno.cod.platform.server.core.domain.Result;

import java.util.List;
import java.util.UUID;

public interface ResultRepository extends JpaRepository<Result, UUID> {

    @Query("SELECT result FROM Result result " +
            "LEFT JOIN FETCH result.challenge challenge " +
            "LEFT JOIN FETCH challenge.challengeTemplate template " +
            "LEFT JOIN FETCH template.tasks " +
            "WHERE result.id = :id")
    Result findOneWithChallenge(@Param("id") UUID id);

    @Query("SELECT result FROM Result result " +
            "LEFT JOIN FETCH result.challenge challenge " +
            "LEFT JOIN FETCH result.user user " +
            "WHERE user.id = :user AND challenge.id = :challenge")
    Result findOneByUserAndChallenge(@Param("user") UUID userId, @Param("challenge") UUID challengeId);

    @Query("SELECT result FROM Result result " +
            "JOIN result.challenge challenge " +
            "JOIN challenge.challengeTemplate template " +
            "WHERE template.id = :templateId ")
    List<Result> findAllByTemplate(@Param("template") UUID templateId);

    @Query("SELECT result FROM Result result " +
            "JOIN result.challenge challenge " +
            "WHERE challenge.id = :id ")
    List<Result> findAllByChallenge(@Param("id") UUID id);
}
