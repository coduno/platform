package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.dto.participation.invitation.ParticipationInvitationCreateDto;
import uno.cod.platform.server.core.service.ParticipationInvitationService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;

@RestController
public class ParticipationInvitationController {
    private final ParticipationInvitationService service;

    @Autowired
    public ParticipationInvitationController(ParticipationInvitationService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.PARTICIPATION_INVITATIONS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createInvitation(@Valid @RequestBody ParticipationInvitationCreateDto dto) {
        service.save(dto);
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }
}
