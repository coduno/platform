package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.dto.location.LocationShowDto;
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

    public List<LocationShowDto> findLocations(String challengeCanonicalName) {
        return repository.findAllByChallengeCanonicalName(challengeCanonicalName)
                .stream()
                .map(LocationShowDto::new)
                .collect(Collectors.toList());
    }
}
