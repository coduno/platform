package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.Location;
import uno.cod.platform.server.core.dto.location.LocationCreateDto;
import uno.cod.platform.server.core.dto.location.LocationShowDto;
import uno.cod.platform.server.core.exception.CodunoResourceConflictException;
import uno.cod.platform.server.core.repository.LocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {
    private final LocationRepository repository;

    @Autowired
    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public void createFromDto(LocationCreateDto dto) {
        Location location = repository.findOneByName(dto.getName());
        if (location != null) {
            throw new CodunoResourceConflictException("location.name.existing", new String[]{dto.getName()});
        }
        if (dto.getPlaceId() != null) {
            location = repository.findOneByPlaceId(dto.getPlaceId());
            if (location != null) {
                throw new CodunoResourceConflictException("location.placeid.existing", new String[]{dto.getPlaceId(), location.getName()});
            }
        }
        location = new Location();
        location.setName(dto.getName());
        location.setPlaceId(dto.getPlaceId());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        repository.save(location);
    }

    public Location findOrCreate(LocationCreateDto dto) {
        Location location = repository.findOneByPlaceId(dto.getPlaceId());
        if (location != null) {
            return location;
        }
        location = new Location();
        location.setName(dto.getName());
        location.setPlaceId(dto.getPlaceId());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        return repository.save(location);
    }

    public List<LocationShowDto> findLocations() {
        return repository.findAll()
                .stream()
                .map(LocationShowDto::new)
                .collect(Collectors.toList());
    }

    public List<LocationShowDto> findLocations(String challengeCanonicalName) {
        return repository.findAllByChallengeCanonicalName(challengeCanonicalName)
                .stream()
                .map(LocationShowDto::new)
                .collect(Collectors.toList());
    }
}
