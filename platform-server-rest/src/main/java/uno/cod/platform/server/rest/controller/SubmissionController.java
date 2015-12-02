package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.core.service.SubmissionService;
import uno.cod.platform.server.rest.RestUrls;

import java.io.IOException;

@RestController
public class SubmissionController {
    private final SubmissionService service;

    @Autowired
    public SubmissionController(SubmissionService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.RESULTS_TASKS_SUBMISSIONS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> create(@PathVariable Long resultId,
                                         @PathVariable Long taskId,
                                         @RequestBody MultipartFile file) throws IOException {
        service.create(resultId, taskId, file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
