package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.dto.location.LocationShowDto;
import uno.cod.platform.server.core.service.LocationService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.List;

@RestController
public class LocationController {
    private final LocationService service;

    @Autowired
    public LocationController(LocationService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_LOCATIONS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LocationShowDto>> getLocationsForChallenge(@PathVariable String canonicalName) {
        return new ResponseEntity<>(service.findLocations(canonicalName), HttpStatus.OK);
    }
}
