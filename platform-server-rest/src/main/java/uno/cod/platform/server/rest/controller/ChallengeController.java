package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeShowDto;
import uno.cod.platform.server.core.service.ChallengeService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.List;

@RestController
public class ChallengeController {
    private final ChallengeService service;

    @Autowired
    public ChallengeController(ChallengeService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.CHALLENGES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationMember(principal, #dto.organizationId)")
    public ResponseEntity<Long> create(@RequestBody ChallengeCreateDto dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.CHALLENGES, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationMember(principal, #organizationId)")
    public ResponseEntity<List<ChallengeShowDto>> list(@RequestParam("organization") Long organizationId) {
        return new ResponseEntity<>(service.findAll(organizationId), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canGetChallenge(principal, #id, #organizationId)")
    public ResponseEntity<ChallengeShowDto> get(@PathVariable Long id, @RequestParam("organization") Long organizationId) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }
}
