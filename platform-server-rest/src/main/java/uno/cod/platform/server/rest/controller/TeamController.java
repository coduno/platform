package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.team.TeamCreateDto;
import uno.cod.platform.server.core.dto.team.TeamShowDto;
import uno.cod.platform.server.core.service.TeamInvitationService;
import uno.cod.platform.server.core.service.TeamService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TeamController {
    private final TeamService service;
    private final TeamInvitationService teamInvitationService;

    @Autowired
    public TeamController(TeamService service, TeamInvitationService teamInvitationService) {
        this.service = service;
        this.teamInvitationService = teamInvitationService;
    }

    @RequestMapping(value = RestUrls.TEAMS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> create(@Valid @RequestBody TeamCreateDto dto) {
        service.create(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.TEAMS_CANONICAL_NAME, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamShowDto> getOne(@PathVariable("canonicalName") String canonicalName) {
        return new ResponseEntity<>(service.findOne(canonicalName), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.TEAMS_CANONICAL_NAME, method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated() and @securityService.isTeamAdmin(principal, #canonicalName)")
    public ResponseEntity<String> delete(@PathVariable("canonicalName") String canonicalName) {
        service.delete(canonicalName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USERS_USERNAME_TEAMS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamShowDto>> findByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(service.findAllTeamsForUser(username), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_TEAMS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamShowDto>> findMyTeams(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(service.findAllTeamsForUser(user.getUsername()), HttpStatus.OK);
    }
}