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
import uno.cod.platform.server.core.service.TeamService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class TeamController {
    private final TeamService service;

    @Autowired
    public TeamController(TeamService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.TEAMS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> create(@Valid @RequestBody TeamCreateDto dto) {
        service.create(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.TEAMS_ID_JOIN, method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated() and @securityService.canJoinTeam(principal, #teamId)")
    public ResponseEntity<String> joinTeam(@PathVariable("id") UUID teamId, @AuthenticationPrincipal User user) {
        service.join(user, teamId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.USER_TEAMS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamShowDto>> findMyTeams(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(service.findAllTeamsForUser(user.getId()), HttpStatus.OK);
    }
}
