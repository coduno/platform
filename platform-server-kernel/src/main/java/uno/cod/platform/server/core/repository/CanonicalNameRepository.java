package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.CanonicalName;

@Repository
public interface CanonicalNameRepository extends JpaRepository<CanonicalName, String> {
}
