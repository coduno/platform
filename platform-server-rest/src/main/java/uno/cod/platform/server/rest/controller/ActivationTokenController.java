package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.dto.user.ActivationTokenCreateDto;
import uno.cod.platform.server.core.service.ActivationTokenService;
import uno.cod.platform.server.rest.RestUrls;

import javax.mail.MessagingException;
import javax.validation.Valid;

@RestController
public class ActivationTokenController {
    @Autowired
    private ActivationTokenService activationTokenService;

    @RequestMapping(value = RestUrls.ACTIVATION_TOKENS, method = RequestMethod.POST)
    public ResponseEntity<String> create(@Valid @RequestBody ActivationTokenCreateDto dto) throws MessagingException {
        activationTokenService.createActivationTokenFromDto(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
