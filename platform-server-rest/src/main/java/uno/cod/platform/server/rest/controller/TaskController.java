package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.security.AllowedForAdmin;
import uno.cod.platform.server.core.service.TaskService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(value = RestUrls.TASKS, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #dto.organizationId)")
    public ResponseEntity<UUID> create(@Valid @RequestBody TaskCreateDto dto) {
        return new ResponseEntity<>(taskService.save(dto), HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.TASKS_ID, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and @securityService.canAccessTask(principal, #id)")
    public ResponseEntity<TaskShowDto> findById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(taskService.findById(id, user), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.TASKS, method = RequestMethod.GET, params = {"organization"})
    @PreAuthorize("isAuthenticated() and @securityService.isOrganizationAdmin(principal, #organization)")
    public ResponseEntity<List<TaskShowDto>> findAllForOrganization(@RequestParam("organization") UUID organization) {
        return new ResponseEntity<>(taskService.findAllForOrganization(organization), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.TASKS, method = RequestMethod.GET)
    @AllowedForAdmin
    public ResponseEntity<List<TaskShowDto>> findAll() {
        return new ResponseEntity<>(taskService.findAll(), HttpStatus.OK);
    }
}