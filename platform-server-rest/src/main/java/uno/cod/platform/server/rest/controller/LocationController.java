package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.location.LocationCreateDto;
import uno.cod.platform.server.core.dto.location.LocationShowDto;
import uno.cod.platform.server.core.security.AllowedForAdmin;
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

    @RequestMapping(value = RestUrls.LOCATIONS, method = RequestMethod.POST)
    @AllowedForAdmin
    public ResponseEntity create(@RequestBody LocationCreateDto dto) {
        service.createFromDto(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.LOCATIONS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LocationShowDto>> getLocations() {
        return new ResponseEntity<>(service.findLocations(), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_LOCATIONS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LocationShowDto>> getLocationsForChallenge(@RequestParam String canonicalName) {
        return new ResponseEntity<>(service.findLocations(canonicalName), HttpStatus.OK);
    }
}
