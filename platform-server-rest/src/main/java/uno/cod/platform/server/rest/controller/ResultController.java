package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.dto.result.ResultShowDto;
import uno.cod.platform.server.core.service.ResultService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.constraints.NotNull;
import java.security.Principal;

@RestController
public class ResultController {
    private final ResultService service;

    @Autowired
    public ResultController(ResultService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.RESULTS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultShowDto> create(@RequestBody @NotNull Long challengeId, Principal principal) {
        return new ResponseEntity<>(service.save(challengeId, principal.getName()), HttpStatus.OK);
    }
}
