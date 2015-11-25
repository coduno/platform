package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.organizationMember.OrganizationMemberCreateDto;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.service.OrganizationMemberService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;

@RestController
public class OrganizationMemberController {
    private final OrganizationMemberService service;

    @Autowired
    public OrganizationMemberController(OrganizationMemberService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.ORGANIZATION_MEMBERS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #id)")
    public ResponseEntity<String> create(@PathVariable Long id, @Valid @RequestBody OrganizationMemberCreateDto dto) {
        service.save(dto, id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @RequestMapping(value = RestUrls.ORGANIZATION_MEMBERS, method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #id)")
    public ResponseEntity<String> delete(@PathVariable Long id, @Valid @RequestBody OrganizationMemberCreateDto dto) {
        service.delete(dto, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
