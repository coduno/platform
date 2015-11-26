package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uno.cod.platform.server.core.domain.Result;

public interface ResultRepository extends JpaRepository<Result, Long>{
}
