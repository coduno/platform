package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateCreateDto;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateShowDto;
import uno.cod.platform.server.core.service.ChallengeTemplateService;
import uno.cod.platform.server.core.service.SessionService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.List;
import java.util.UUID;

@RestController
public class ChallengeTemplateController {
    private final ChallengeTemplateService service;
    private final SessionService sessionService;

    @Autowired
    public ChallengeTemplateController(ChallengeTemplateService service, SessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.isActiveOrganizationAdmin(principal)")
    public ResponseEntity<UUID> create(@RequestBody ChallengeTemplateCreateDto dto) {
        return new ResponseEntity<>(service.save(dto, sessionService.getActiveOrganization()), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.isActiveOrganizationAdmin(principal)")
    public ResponseEntity<List<ChallengeTemplateShowDto>> list() {
        return new ResponseEntity<>(service.findAll(sessionService.getActiveOrganization()), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #id)")
    public ResponseEntity<ChallengeTemplateShowDto> get(@PathVariable UUID id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_ID_TEMPLATE, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessScheduledChallengeChallenge(principal, #id)")
    public ResponseEntity<ChallengeTemplateShowDto> getByChallengeId(@PathVariable UUID id) {
        return new ResponseEntity<>(service.findByChallengeId(id), HttpStatus.OK);
    }
}
