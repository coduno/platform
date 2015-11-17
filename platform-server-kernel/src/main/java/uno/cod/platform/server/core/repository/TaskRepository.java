package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uno.cod.platform.server.core.domain.Task;

/**
 * Created by vbalan on 11/17/2015.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
