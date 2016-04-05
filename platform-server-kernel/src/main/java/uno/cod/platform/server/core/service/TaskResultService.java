package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.domain.TaskResult;
import uno.cod.platform.server.core.domain.TaskResultKey;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.TaskRepository;
import uno.cod.platform.server.core.repository.TaskResultRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskResultService {

    private final TaskResultRepository taskResultRepository;
    private final ResultRepository resultRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskResultService(TaskResultRepository taskResultRepository,
                             ResultRepository resultRepository,
                             TaskRepository taskRepository) {
        this.taskResultRepository = taskResultRepository;
        this.resultRepository = resultRepository;
        this.taskRepository = taskRepository;
    }

    public void startTask(UUID resultId, UUID taskId) throws IllegalArgumentException {
        Result result = resultRepository.findOneWithChallenge(resultId);
        if (result == null) {
            throw new AccessDeniedException("result.invalid");
        }
        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            throw new AccessDeniedException("task.invalid");
        }

        TaskResult taskResult = taskResultRepository.findOneByTaskAndResult(taskId, resultId);
        if (taskResult != null) {
            if (taskResult.getStartTime() != null) {
                throw new AccessDeniedException("task.denied");
            }
            return;
        }

        TaskResultKey key = new TaskResultKey();
        key.setTask(task);
        key.setResult(result);

        taskResult = new TaskResult();
        taskResult.setKey(key);
        taskResult.setStartTime(ZonedDateTime.now());
        taskResultRepository.save(taskResult);
    }

    public void finishTaskResult(TaskResult taskResult, ZonedDateTime endTime, boolean green) {
        taskResult.setEndTime(endTime);
        taskResult.setSuccessful(green);
        taskResultRepository.save(taskResult);

        Result result = taskResult.getKey().getResult();
        Task task = taskResult.getKey().getTask();

        List<Task> tasks = result.getChallenge().getChallengeTemplate().getTasks();

        if (tasks.indexOf(task) == tasks.size() - 1) {
            result.setFinished(endTime);
            resultRepository.save(result);
        }
    }

    public TaskResult findByTaskAndResult(UUID taskId, UUID resultId) {
        return taskResultRepository.findOneByTaskAndResult(taskId, resultId);
    }
}
