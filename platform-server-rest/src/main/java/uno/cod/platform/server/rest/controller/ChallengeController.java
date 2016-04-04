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
import uno.cod.platform.server.core.dto.challenge.UserChallengeShowDto;
import uno.cod.platform.server.core.service.ChallengeService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class ChallengeController {

    private final ChallengeService service;

    @Autowired
    public ChallengeController(ChallengeService challengeService){
        this.service = challengeService;
    }

    @RequestMapping(value = RestUrls.CHALLENGES_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #id)")
    public ResponseEntity<ChallengeDto> get(@PathVariable UUID id){
        return new ResponseEntity<>(service.findOneById(id), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.CHALLENGES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #dto.templateId)")
    public ResponseEntity<UUID> createChallenge(@Valid @RequestBody ChallengeCreateDto dto){
        return new ResponseEntity<>(service.createFromDto(dto), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.USER_CHALLENGES, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserChallengeShowDto>> getUserChallenges(@AuthenticationPrincipal User user){
        return new ResponseEntity<>(service.getUserChallenges(user), HttpStatus.OK);
    }
}
