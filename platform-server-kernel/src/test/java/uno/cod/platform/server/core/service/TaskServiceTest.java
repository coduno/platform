package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.repository.EndpointRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.RunnerRepository;
import uno.cod.platform.server.core.repository.TaskRepository;
import uno.cod.platform.server.core.service.util.TaskTestUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TaskServiceTest {
    private TaskService service;
    private TaskRepository repository;
    private EndpointRepository endpointRepository;
    private OrganizationRepository organizationRepository;
    private RunnerRepository runnerRepository;

    @Before
    public void setup() {
        this.repository = Mockito.mock(TaskRepository.class);
        this.endpointRepository = Mockito.mock(EndpointRepository.class);
        this.organizationRepository = Mockito.mock(OrganizationRepository.class);
        this.runnerRepository = Mockito.mock(RunnerRepository.class);
        this.service = new TaskService(repository, endpointRepository, organizationRepository, runnerRepository);
    }

    @Test
    // TODO invalid save methods
    public void save() throws Exception {
        TaskCreateDto dto = TaskTestUtil.getTaskCreateDto();
        Task task = TaskTestUtil.getTask(dto);
        task.setId(UUID.randomUUID());

        Mockito.when(repository.save(Mockito.any(Task.class))).thenReturn(task);
        Mockito.when(endpointRepository.findOne(dto.getEndpointId())).thenReturn(task.getEndpoint());
        Mockito.when(runnerRepository.findOne(dto.getRunnerId())).thenReturn(task.getRunner());
        Mockito.when(organizationRepository.findOne(dto.getOrganizationId())).thenReturn(task.getOrganization());

        UUID id = service.save(dto);
        Assert.assertEquals(id, task.getId());
    }

    @Test
    public void findById() throws Exception {
        Task task = TaskTestUtil.getValidTask();
        Mockito.when(repository.findOneWithTemplates(task.getId())).thenReturn(task);

        TaskShowDto dto = service.findById(task.getId());

        assertTaskEquals(task, dto);
    }

    @Test
    public void findAll() throws Exception {
        Task task = TaskTestUtil.getValidTask();
        List<Task> tasks = Collections.singletonList(task);

        Mockito.when(repository.findAllByOrganizationIdWithEndpoints(task.getOrganization().getId())).thenReturn(tasks);

        List<TaskShowDto> dtos = service.findAllForOrganization(task.getOrganization().getId());

        Assert.assertEquals(dtos.size(), tasks.size());
        TaskShowDto dto = dtos.get(0);

        assertTaskEquals(task, dto);
    }

    public void assertTaskEquals(Task task, TaskShowDto dto) {
        Assert.assertEquals(dto.getId(), task.getId());
        Assert.assertEquals(dto.getName(), task.getName());
        Assert.assertEquals(dto.getDescription(), task.getDescription());
        Assert.assertEquals(dto.getInstructions(), task.getInstructions());
        Assert.assertEquals(dto.getDuration(), task.getDuration());
        Assert.assertEquals(dto.getSkillMap(), task.getSkillMap());

        Assert.assertEquals(dto.getLanguages().size(), task.getLanguages().size());
        Assert.assertEquals(dto.getLanguages().get(0).getId(), task.getLanguages().iterator().next().getId());

        Assert.assertEquals(dto.getEndpoint().getId(), task.getEndpoint().getId());
    }
}