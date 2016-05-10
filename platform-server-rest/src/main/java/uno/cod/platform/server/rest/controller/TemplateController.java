package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.core.service.TemplateService;
import uno.cod.platform.server.rest.RestUrls;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

@RestController
public class TemplateController {
    private final TemplateService service;

    @Autowired
    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @RequestMapping(path = RestUrls.TASKS_ID_TEMPLATES, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> save(
            @PathVariable UUID id,
            @RequestParam("languageObjectName") String objectName,
            @RequestParam("languageReadableName") String readableName,
            @RequestParam("file") MultipartFile file) {
        service.save(id, objectName, readableName, file);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @RequestMapping(path = RestUrls.TASKS_ID_TEMPLATES_NAME, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getTemplateUrl(@PathVariable UUID id,
                                                       @PathVariable String name) throws GeneralSecurityException, IOException {
        return new ResponseEntity<>(service.getTemplateUrls(id, name), HttpStatus.OK);
    }
}
