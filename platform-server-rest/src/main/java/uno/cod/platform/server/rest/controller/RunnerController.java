package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.dto.runner.RunnerShowDto;
import uno.cod.platform.server.core.security.AllowedForAdmin;
import uno.cod.platform.server.core.service.RunnerService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.List;

@RestController
public class RunnerController {
    private final RunnerService service;

    @Autowired
    public RunnerController(RunnerService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.RUNNERS, method = RequestMethod.GET)
    @AllowedForAdmin
    public ResponseEntity<List<RunnerShowDto>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

}
