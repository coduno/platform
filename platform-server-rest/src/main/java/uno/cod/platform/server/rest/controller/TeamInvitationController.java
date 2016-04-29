package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.team.invitation.TeamInvitationCreateDto;
import uno.cod.platform.server.core.dto.team.invitation.TeamInvitationShowDto;
import uno.cod.platform.server.core.service.TeamInvitationService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TeamInvitationController {
    private final TeamInvitationService service;

    @Autowired
    public TeamInvitationController(TeamInvitationService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.TEAMS_CANONICAL_NAME_INVITATIONS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.isTeamAdmin(principal, #canonicalName)")
    public ResponseEntity<String> create(@Valid @RequestBody TeamInvitationCreateDto dto,
                                         @PathVariable("canonicalName") String canonicalName,
                                         @AuthenticationPrincipal User user) {
        service.create(user, dto.getUsername(), canonicalName);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.USER_TEAMS_INVITATIONS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamInvitationShowDto>> findMyTeamInvitations(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(service.findInvitationsByUserId(user.getId()), HttpStatus.CREATED);
    }
}
