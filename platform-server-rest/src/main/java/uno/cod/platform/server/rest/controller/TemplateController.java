package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.core.service.TemplateService;
import uno.cod.platform.server.rest.RestUrls;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.UUID;

@RestController
public class TemplateController {
    private final TemplateService service;

    @Autowired
    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @RequestMapping(path = RestUrls.TEMPLATES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> save(
            @RequestParam("taskId") UUID task,
            @RequestParam("languageId") UUID language,
            @RequestParam("file") MultipartFile file) {
        service.save(task, language, file);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @RequestMapping(path = RestUrls.TEMPLATES_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getTemplateUrl(@PathVariable UUID id) throws GeneralSecurityException, UnsupportedEncodingException {
        return new ResponseEntity<>(service.getTemplateUrl(id), HttpStatus.OK);
    }
}
