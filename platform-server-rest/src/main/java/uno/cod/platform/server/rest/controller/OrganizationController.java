package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.organization.OrganizationCreateDto;
import uno.cod.platform.server.core.dto.organization.OrganizationShowDto;
import uno.cod.platform.server.core.dto.organization.member.OrganizationMembershipShowDto;
import uno.cod.platform.server.core.service.OrganizationService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
public class OrganizationController {
    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @RequestMapping(value = RestUrls.ORGANIZATIONS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> create(@Valid @RequestBody OrganizationCreateDto createDto, Principal principal) {
        organizationService.createFromDto(createDto, principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.ORGANIZATIONS, method = RequestMethod.GET)
    public ResponseEntity<List<OrganizationShowDto>> list() {
        return new ResponseEntity<>(organizationService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.ORGANIZATIONS_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationMember(principal, #id)")
    public ResponseEntity<OrganizationShowDto> get(@PathVariable UUID id) {
        return new ResponseEntity<>(organizationService.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.ORGANIZATIONS_ID, method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #organizationId)")
    public String delete(@PathVariable("id") UUID organizationId) {
        return "";
    }

    @RequestMapping(value = RestUrls.USER_ORGANIZATIONS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrganizationMembershipShowDto>> ownOrganizations(Principal principal) {
        return new ResponseEntity<>(organizationService.findUserAdminOrganizations(principal.getName()), HttpStatus.OK);
    }
}
