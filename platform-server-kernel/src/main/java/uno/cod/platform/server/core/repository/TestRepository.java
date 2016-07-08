package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Test;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {
    @Query("SELECT test FROM Test test " +
            "LEFT JOIN FETCH test.task task " +
            "WHERE task.id = :taskId " +
            "ORDER BY test.index ASC")
    List<Test> findByTaskIdOrderByIndex(@Param("taskId") UUID taskId);

    @Query("SELECT test FROM Test test " +
            "LEFT JOIN FETCH test.runner " +
            "WHERE test.id = :id")
    Test findOneWithRunner(@Param("id") UUID id);
}
