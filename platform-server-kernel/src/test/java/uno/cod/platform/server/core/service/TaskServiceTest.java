package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.exception.CodunoAccessDeniedException;
import uno.cod.platform.server.core.repository.*;
import uno.cod.platform.server.core.service.util.ChallengeTestUtil;
import uno.cod.platform.server.core.service.util.TaskTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TaskServiceTest {
    private TaskService service;
    private TaskRepository repository;
    private EndpointRepository endpointRepository;
    private OrganizationRepository organizationRepository;
    private RunnerRepository runnerRepository;
    private ChallengeRepository challengeRepository;
    private UserRepository userRepository;
    private ResultRepository resultRepository;
    private HttpSession httpSession;

    @Before
    public void setup() {
        this.repository = Mockito.mock(TaskRepository.class);
        this.endpointRepository = Mockito.mock(EndpointRepository.class);
        this.organizationRepository = Mockito.mock(OrganizationRepository.class);
        this.runnerRepository = Mockito.mock(RunnerRepository.class);
        this.challengeRepository = Mockito.mock(ChallengeRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.resultRepository = Mockito.mock(ResultRepository.class);
        this.httpSession = Mockito.mock(HttpSession.class);
        this.service = new TaskService(repository, endpointRepository, organizationRepository, runnerRepository, challengeRepository, userRepository, resultRepository, httpSession);
    }

    @Test
    // TODO invalid save methods
    public void save() throws Exception {
        TaskCreateDto dto = TaskTestUtil.getTaskCreateDto();
        Task task = TaskTestUtil.getTask(dto);

        Mockito.when(repository.save(Mockito.any(Task.class))).thenReturn(task);
        Mockito.when(endpointRepository.findOne(dto.getEndpointId())).thenReturn(task.getEndpoint());
        Mockito.when(runnerRepository.findOne(dto.getRunnerId())).thenReturn(task.getRunner());
        Mockito.when(organizationRepository.findOne(dto.getOrganizationId())).thenReturn(task.getOrganization());

        UUID id = service.save(dto);
        Assert.assertEquals(id, task.getId());
    }


    @Test(expected = CodunoAccessDeniedException.class)
    public void findByIdAccessDenied() throws Exception {
        // Neither the session attribute is set nor the repository methods are mocked and therefore return null
        Task task = TaskTestUtil.getValidTask();
        User user = UserTestUtil.getUser();

        Mockito.when(repository.findOneWithTemplates(task.getId())).thenReturn(task);

        TaskShowDto dto = service.findById(task.getId(), user);
        assertTaskEquals(task, dto);
    }

    @Test
    public void findByIdAccessGranted() throws Exception {
        Task task = TaskTestUtil.getValidTask();
        Task newTask = TaskTestUtil.getValidTask();
        User user = UserTestUtil.getUser();
        Challenge challenge = ChallengeTestUtil.getChallenge();

        Result result = new Result();
        result.setChallenge(challenge);
        result.setUser(user);

        TaskResult taskResult = new TaskResult();
        taskResult.setSuccessful(true);

        TaskResultKey taskResultKey = new TaskResultKey();
        taskResultKey.setResult(result);
        taskResultKey.setTask(task);

        ArrayList<TaskResult> taskResultList = new ArrayList<>();
        taskResultList.add(taskResult);
        result.setTaskResults(taskResultList);

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(newTask);
        challenge.getChallengeTemplate().setTasks(tasks);

        Mockito.when(httpSession.getAttribute(ResultService.CURRENT_CHALLENGE)).thenReturn(challenge.getId());
        Mockito.when(userRepository.findOne(user.getId())).thenReturn(user);
        Mockito.when(repository.findOneWithTemplates(task.getId())).thenReturn(task);
        Mockito.when(repository.findOneWithTemplates(newTask.getId())).thenReturn(newTask);
        Mockito.when(challengeRepository.findOne(challenge.getId())).thenReturn(challenge);
        Mockito.when(resultRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(result);

        TaskShowDto dto = service.findById(newTask.getId(), user);
        assertTaskEquals(newTask, dto);
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