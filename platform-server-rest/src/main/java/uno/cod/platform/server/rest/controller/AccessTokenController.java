package uno.cod.platform.server.rest.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.domain.AccessToken;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.accesstoken.AccessTokenDto;
import uno.cod.platform.server.core.dto.user.accesstoken.CreateAccessTokenDto;
import uno.cod.platform.server.core.dto.user.accesstoken.CreatedAccessTokenDto;
import uno.cod.platform.server.core.dto.user.accesstoken.DeleteAccessTokenDto;
import uno.cod.platform.server.core.service.AccessTokenService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.Set;

@RestController
public class AccessTokenController {
    private final AccessTokenService accessTokenService;

    @Autowired
    public AccessTokenController(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @ApiOperation(value = "Lists all access tokens",
            notes = "all da tokens!")
    @RequestMapping(value = RestUrls.USER_ACCCESSTOKEN, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<AccessTokenDto>> list(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(accessTokenService.listAllAccessTokensForUser(user), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_ACCCESSTOKEN, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CreatedAccessTokenDto> create(@Valid @RequestBody CreateAccessTokenDto dto, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(accessTokenService.createToken(dto.getComment(), user), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_ACCCESSTOKEN, method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> delete(@Valid @RequestBody DeleteAccessTokenDto dto, @AuthenticationPrincipal User user) {
        accessTokenService.deleteToken(dto.getId(), user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
