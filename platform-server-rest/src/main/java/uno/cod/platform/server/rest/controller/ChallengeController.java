package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.service.ChallengeService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;

@RestController
public class ChallengeController {

    private final ChallengeService service;

    @Autowired
    public ChallengeController(ChallengeService challengeService){
        this.service = challengeService;
    }

    @RequestMapping(value = RestUrls.CHALLENGES_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #id)")
    public ResponseEntity<ChallengeDto> get(@PathVariable Long id){
        return new ResponseEntity<>(service.findOneById(id), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.CHALLENGES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessChallenge(principal, #id)")
    public ResponseEntity createChallenge(@Valid @RequestBody ChallengeCreateDto dto){
        service.createFromDto(dto);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
