package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.service.TaskResultService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.UUID;

@RestController
public class TaskResultController {

    private final TaskResultService taskResultService;

    @Autowired
    public TaskResultController(TaskResultService taskResultService){
        this.taskResultService = taskResultService;
    }

    @RequestMapping(value = RestUrls.RESULTS_ID_TASK_ID, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> start(@PathVariable("id") UUID resultId, @PathVariable("taskId") UUID taskId) {
        taskResultService.startTask(resultId, taskId);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
