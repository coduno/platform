package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.participation.ParticipationShowDto;
import uno.cod.platform.server.core.dto.participation.invitation.ParticipationInvitationCreateDto;
import uno.cod.platform.server.core.service.ParticipationInvitationService;
import uno.cod.platform.server.rest.RestUrls;

import javax.mail.MessagingException;
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
    public ResponseEntity<String> createInvitation(@Valid @RequestBody ParticipationInvitationCreateDto dto) throws MessagingException {
        service.save(dto);
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.PARTICIPATION_INVITATIONS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ParticipationShowDto> getByKey(@RequestParam("username") String username,
                                                         @RequestParam("challenge") String challenge) {
        return new ResponseEntity<>(service.get(username, challenge), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.PARTICIPATION_INVITATIONS_ACCEPT, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> acceptInvitation(@RequestParam("username") String username,
                                                   @RequestParam("challenge") String challenge,
                                                   @AuthenticationPrincipal User user) throws MessagingException {
        service.accept(user, username, challenge);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
