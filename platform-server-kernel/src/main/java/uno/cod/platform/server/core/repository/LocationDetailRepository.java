package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.ChallengeLocationKey;
import uno.cod.platform.server.core.domain.LocationDetail;

@Repository
public interface LocationDetailRepository extends JpaRepository<LocationDetail, ChallengeLocationKey> {
}
