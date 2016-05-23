package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Runner;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.test.TestCreateDto;
import uno.cod.platform.server.core.dto.test.TestShowDto;
import uno.cod.platform.server.core.repository.RunnerRepository;
import uno.cod.platform.server.core.repository.TaskRepository;
import uno.cod.platform.server.core.repository.TestRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestServiceTest {
    private TestService service;
    private TestRepository repository;
    private TaskRepository taskRepository;
    private RunnerRepository runnerRepository;

    @Before
    public void setUp() throws Exception {
        this.repository = Mockito.mock(TestRepository.class);
        this.taskRepository = Mockito.mock(TaskRepository.class);
        this.runnerRepository = Mockito.mock(RunnerRepository.class);

        this.service = new TestService(repository, taskRepository, runnerRepository);
    }

    @Test
    public void save() throws Exception {
        TestCreateDto dto = new TestCreateDto();
        dto.setTaskId(UUID.randomUUID());
        dto.setRunnerId(UUID.randomUUID());

        Mockito.when(taskRepository.findOne(dto.getTaskId())).thenReturn(new Task());
        Mockito.when(runnerRepository.findOne(dto.getRunnerId())).thenReturn(new Runner());

        service.save(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveInvalidTask() throws Exception {
        TestCreateDto dto = new TestCreateDto();
        dto.setTaskId(UUID.randomUUID());
        dto.setRunnerId(UUID.randomUUID());

        Mockito.when(taskRepository.findOne(dto.getTaskId())).thenReturn(null);
        Mockito.when(runnerRepository.findOne(dto.getRunnerId())).thenReturn(new Runner());

        service.save(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveInvalidRunner() throws Exception {
        TestCreateDto dto = new TestCreateDto();
        dto.setTaskId(UUID.randomUUID());
        dto.setRunnerId(UUID.randomUUID());

        Mockito.when(taskRepository.findOne(dto.getTaskId())).thenReturn(new Task());
        Mockito.when(runnerRepository.findOne(dto.getRunnerId())).thenReturn(null);

        service.save(dto);
    }

    @Test
    public void findByTaskIdOrderByIndex() throws Exception {
        uno.cod.platform.server.core.domain.Test test = new uno.cod.platform.server.core.domain.Test();
        test.setId(UUID.randomUUID());

        Mockito.when(repository.findByTaskIdOrderByIndex(test.getId())).thenReturn(Collections.singletonList(test));

        List<TestShowDto> dtos = service.findByTaskIdOrderByIndex(test.getId());

        Assert.assertEquals(dtos.size(), 1);
        TestShowDto dto = dtos.get(0);
        Assert.assertEquals(dto.getId(), test.getId());
    }
}