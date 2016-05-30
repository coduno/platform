package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.invitation.InvitationDto;
import uno.cod.platform.server.core.dto.invitation.InvitationShowDto;
import uno.cod.platform.server.core.service.InvitationService;
import uno.cod.platform.server.rest.RestUrls;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Set;
import java.util.UUID;

@RestController
public class InvitationController {
    private final InvitationService service;

    @Autowired
    public InvitationController(InvitationService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.INVITE, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> invite(@Valid @RequestBody InvitationDto dto,
                                         Principal principal) throws MessagingException {
        service.invite(dto, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_INVITATIONS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<InvitationShowDto>> getByChallenge(@PathVariable String canonicalName) {
        return new ResponseEntity<>(service.getByChallengeCanonicalName(canonicalName), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.INVITE_AUTH_TOKEN, method = RequestMethod.GET)
    public ResponseEntity<UUID> authByToken(@PathVariable String token) {
        return new ResponseEntity<>(service.authByToken(token), HttpStatus.OK);
    }

}
