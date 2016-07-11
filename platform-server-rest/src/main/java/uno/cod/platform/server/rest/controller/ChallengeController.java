package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.NameDto;
import uno.cod.platform.server.core.dto.challenge.*;
import uno.cod.platform.server.core.dto.location.LocationUpdateDto;
import uno.cod.platform.server.core.dto.participation.ParticipationShowDto;
import uno.cod.platform.server.core.service.ChallengeService;
import uno.cod.platform.server.core.service.ParticipationService;
import uno.cod.platform.server.rest.RestUrls;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
public class ChallengeController {
    private final ChallengeService challengeService;
    private final ParticipationService participationService;

    @Autowired
    public ChallengeController(ChallengeService challengeService,
                               ParticipationService participationService) {
        this.challengeService = challengeService;
        this.participationService = participationService;
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME, method = RequestMethod.GET)
    @PreAuthorize("@securityService.canAccessChallenge(principal, #canonicalName)")
    public ResponseEntity<ChallengeDto> getByCanonicalName(@PathVariable String canonicalName) {
        return new ResponseEntity<>(challengeService.findOneByCanonicalName(canonicalName), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_PARTICIPANTS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #canonicalName)")
    public ResponseEntity<Set<ParticipationShowDto>> getParticipantsByCanonicalName(@PathVariable String canonicalName) {
        return new ResponseEntity<>(participationService.getByChallengeCanonicalName(canonicalName), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallengeTemplate(principal, #dto.templateCanonicalName)")
    public ResponseEntity<NameDto> createChallenge(@Valid @RequestBody ChallengeCreateDto dto) {
        return new ResponseEntity<>(new NameDto(challengeService.createFromDto(dto)), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_PUBLIC, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserChallengeShowDto>> getPublicChallenges(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(challengeService.getPublicChallenges(user), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_REGISTER, method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> register(@PathVariable("canonicalName") String challengeName,
                                           @Valid @RequestBody ParticipationCreateDto dto,
                                           @AuthenticationPrincipal User user) throws MessagingException {
        participationService.registerForChallenge(user, challengeName, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_PARTICIPATION, method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unregister(@PathVariable("canonicalName") String challengeName,
                                             @AuthenticationPrincipal User user) {
        participationService.unregisterFromChallenge(user, challengeName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES, method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NameDto> updateChallengeInfo(@Valid @RequestBody ChallengeUpdateDto dto) {
        return new ResponseEntity<>(new NameDto(challengeService.updateChallengeInfo(dto)), HttpStatus.OK);
    }


    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_LOCATIONS, method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated() and @securityService.canEditChallenge(principal, #canonicalName)")
    public ResponseEntity<String> updateChallengeLocations(@PathVariable String canonicalName, @Valid @RequestBody List<LocationUpdateDto> locations) {
        challengeService.updateLocations(canonicalName, locations);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_CHALLENGE_CANONICAL_NAME_STATUS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #canonicalName)")
    public ResponseEntity<UserChallengeShowDto> getUserStatusByCanonicalName(@PathVariable String canonicalName,
                                                                             @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(challengeService.getChallengeStatusForUser(canonicalName, user), HttpStatus.OK);
    }
}
