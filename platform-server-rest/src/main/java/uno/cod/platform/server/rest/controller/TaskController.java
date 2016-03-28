package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.service.ResultService;
import uno.cod.platform.server.core.service.TaskService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class TaskController {
    private final TaskService taskService;
    private final ResultService resultService;

    @Autowired
    public TaskController(TaskService taskService, ResultService resultService) {
        this.taskService = taskService;
        this.resultService = resultService;
    }

    @RequestMapping(value = RestUrls.TASKS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #dto.organizationId)")
    public ResponseEntity<UUID> create(@Valid @RequestBody TaskCreateDto dto) {
        return new ResponseEntity<>(taskService.save(dto), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.TASKS_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessTask(principal, #id)")
    public ResponseEntity<TaskShowDto> findById(@PathVariable UUID id) {
        return new ResponseEntity<>(taskService.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.TASKS, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #organizationId)")
    public ResponseEntity<List<TaskShowDto>> findAll(@RequestParam("organization") UUID organizationId) {
        return new ResponseEntity<>(taskService.findAll(organizationId), HttpStatus.OK);
    }
}