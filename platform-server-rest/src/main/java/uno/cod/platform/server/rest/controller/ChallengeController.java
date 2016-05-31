package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.dto.challenge.ParticipationCreateDto;
import uno.cod.platform.server.core.dto.challenge.UserChallengeShowDto;
import uno.cod.platform.server.core.dto.participation.ParticipationShowDto;
import uno.cod.platform.server.core.service.ChallengeService;
import uno.cod.platform.server.core.service.ParticipationService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #canonicalName)")
    public ResponseEntity<ChallengeDto> getByCanonicalName(@PathVariable String canonicalName) {
        return new ResponseEntity<>(challengeService.findOneByCanonicalName(canonicalName), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES_CANONICAL_NAME_PARTICIPANTS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #canonicalName)")
    public ResponseEntity<Set<ParticipationShowDto>> getParticipantsByCanonicalName(@PathVariable String canonicalName) {
        return new ResponseEntity<>(participationService.getByChallengeCanonicalName(canonicalName), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallengeTemplate(principal, #dto.templateId)")
    public ResponseEntity<UUID> createChallenge(@Valid @RequestBody ChallengeCreateDto dto) {
        return new ResponseEntity<>(challengeService.createFromDto(dto), HttpStatus.CREATED);
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
                                           @AuthenticationPrincipal User user) {
        participationService.registerForChallenge(user, challengeName, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_CHALLENGE_CANONICAL_NAME_STATUS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #canonicalName)")
    public ResponseEntity<UserChallengeShowDto> getUserStatusByCanonicalName(@PathVariable String canonicalName,
                                                                             @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(challengeService.getChallengeStatusForUser(canonicalName, user), HttpStatus.OK);
    }
}
