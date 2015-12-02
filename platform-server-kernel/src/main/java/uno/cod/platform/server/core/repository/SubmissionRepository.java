package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uno.cod.platform.server.core.domain.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long>{
}
