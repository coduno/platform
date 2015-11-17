package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.service.TaskLoadingService;
import uno.cod.platform.server.core.service.TaskService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by vbalan on 11/17/2015.
 */
@RestController
public class TaskController {
    @Autowired
    private TaskLoadingService taskLoadingService;

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = RestUrls.TASKS, method = RequestMethod.POST)
    public ResponseEntity<Long> create(@Valid @RequestBody TaskCreateDto dto) {
        return new ResponseEntity<>(taskService.create(dto),HttpStatus.CREATED);
    }

    @RequestMapping(value = RestUrls.TASKS_ID, method = RequestMethod.GET)
    public ResponseEntity<TaskShowDto> findById(@PathVariable Long id) {
        return new ResponseEntity<>(taskLoadingService.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.TASKS, method = RequestMethod.GET)
    public ResponseEntity<List<TaskShowDto>> findAll() {
        return new ResponseEntity<>(taskLoadingService.findAll(), HttpStatus.OK);
    }
}
