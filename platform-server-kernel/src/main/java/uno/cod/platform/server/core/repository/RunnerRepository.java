package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Runner;

import java.util.UUID;

@Repository
public interface RunnerRepository extends JpaRepository<Runner, UUID> {
    Runner findOneByPath(String path);
}