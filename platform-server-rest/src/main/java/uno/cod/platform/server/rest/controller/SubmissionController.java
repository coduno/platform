package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.core.domain.User;
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
                                         @RequestParam("language") String language,
                                         @RequestParam("file") MultipartFile file,
                                         @AuthenticationPrincipal User principal) throws IOException {
        service.create(principal, resultId, taskId, file, language);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = RestUrls.RESULTS_TESTS_OUTPUT, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> testOutput(@PathVariable Long resultId,
                                             @PathVariable Long testId,
                                             @RequestParam("file") MultipartFile file,
                                             @AuthenticationPrincipal User principal) throws IOException {
        return new ResponseEntity<>(service.testOutput(principal, resultId, testId, file) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = RestUrls.TASKS_ID_RUN, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> runtask(@PathVariable Long taskId,
                                          @RequestParam("language") String language,
                                          @RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal User principal) throws IOException {
        service.run(principal, taskId, file, language);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
