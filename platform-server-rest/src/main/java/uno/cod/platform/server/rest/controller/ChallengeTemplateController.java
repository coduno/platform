package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateCreateDto;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateShowDto;
import uno.cod.platform.server.core.service.ChallengeTemplateService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.List;

@RestController
public class ChallengeTemplateController {
    private final ChallengeTemplateService service;

    @Autowired
    public ChallengeTemplateController(ChallengeTemplateService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationMember(principal, #dto.organizationId)")
    public ResponseEntity<Long> create(@RequestBody ChallengeTemplateCreateDto dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationMember(principal, #organizationId)")
    public ResponseEntity<List<ChallengeTemplateShowDto>> list(@RequestParam("organization") Long organizationId) {
        return new ResponseEntity<>(service.findAll(organizationId), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #id)")
    public ResponseEntity<ChallengeTemplateShowDto> get(@PathVariable Long id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES_CHALLENGE, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessScheduledChallengeChallenge(principal, #id)")
    public ResponseEntity<ChallengeTemplateShowDto> getByChallengeId(@PathVariable Long id) {
        return new ResponseEntity<>(service.findByChallengeId(id), HttpStatus.OK);
    }
}
