package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateCreateDto;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateShowDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.service.ChallengeTemplateService;
import uno.cod.platform.server.core.service.TaskService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.List;
import java.util.UUID;

@RestController
public class ChallengeTemplateController {
    private final ChallengeTemplateService service;
    private final TaskService taskService;

    @Autowired
    public ChallengeTemplateController(ChallengeTemplateService service, TaskService taskService) {
        this.service = service;
        this.taskService = taskService;
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #dto.organizationId)")
    public ResponseEntity<String> create(@RequestBody ChallengeTemplateCreateDto dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #organization)")
    public ResponseEntity<List<ChallengeTemplateShowDto>> list(@RequestParam("organization") UUID organization) {
        return new ResponseEntity<>(service.findAll(organization), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES_CANONICAL_NAME, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallengeTemplate(principal, #canonicalName)")
    public ResponseEntity<ChallengeTemplateShowDto> get(@PathVariable String canonicalName) {
        return new ResponseEntity<>(service.findByCanonicalName(canonicalName), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGE_TEMPLATES_CANONICAL_NAME_TASKS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallengeTemplate(principal, #canonicalName)")
    public ResponseEntity<List<TaskShowDto>> getTasksForChallengeTemplate(@PathVariable String canonicalName) {
        return new ResponseEntity<>(taskService.findAllForChallengeTemplate(canonicalName), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_TEMPLATE, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #canonicalName)")
    public ResponseEntity<ChallengeTemplateShowDto> getByChallengeCanonicalName(@PathVariable String canonicalName) {
        return new ResponseEntity<>(service.findByChallengeCanonicalName(canonicalName), HttpStatus.OK);
    }
}
