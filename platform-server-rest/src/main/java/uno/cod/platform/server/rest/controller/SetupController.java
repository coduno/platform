package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.service.SetupService;
import uno.cod.platform.server.rest.RestUrls;

@RestController
public class SetupController {
    private final SetupService setupService;

    @Autowired
    public SetupController(SetupService setupService) {
        this.setupService = setupService;
    }

    @RequestMapping(value = RestUrls.SETUP)
    public ResponseEntity<String> setup() {
        setupService.init();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
