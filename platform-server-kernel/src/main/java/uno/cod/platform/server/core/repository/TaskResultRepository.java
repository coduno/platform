package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.TaskResult;
import uno.cod.platform.server.core.domain.TaskResultKey;

import java.util.UUID;

@Repository
public interface TaskResultRepository extends JpaRepository<TaskResult, TaskResultKey> {
    @Query("Select taskResult from TaskResult taskResult " +
            "where taskResult.key.task.id=:task and taskResult.key.result.id=:result")
    TaskResult findOneByTaskAndResult(@Param("task") UUID taskId, @Param("result") UUID resultId);
}
