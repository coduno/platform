package uno.cod.platform.server.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uno.cod.platform.server.core.domain.Location;

import java.util.List;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    Location findOneByName(String name);

    Location findOneByPlaceId(String placeId);

    @Query("SELECT l FROM Location l " +
            "JOIN l.challenges challenge " +
            "WHERE challenge.canonicalName = :canonicalName")
    List<Location> findAllByChallengeCanonicalName(@Param("canonicalName") String canonicalName);
}
