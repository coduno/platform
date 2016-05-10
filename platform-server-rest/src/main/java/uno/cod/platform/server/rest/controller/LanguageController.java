package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.dto.language.LanguageShowDto;
import uno.cod.platform.server.core.security.AllowedForAdmin;
import uno.cod.platform.server.core.service.LanguageService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.List;

@RestController
public class LanguageController {
    private final LanguageService service;

    @Autowired
    public LanguageController(LanguageService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.LANGUAGES, method = RequestMethod.GET)
    @AllowedForAdmin
    public ResponseEntity<List<LanguageShowDto>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }
}
