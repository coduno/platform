package uno.cod.platform.server.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uno.cod.platform.server.core.domain.Location;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}
