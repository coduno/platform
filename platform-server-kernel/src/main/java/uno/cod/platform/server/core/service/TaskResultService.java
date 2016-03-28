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

/**
 * Created by Paul on 3/24/2016.
 */
@Service
@Transactional
public class TaskResultService {

    private final TaskResultRepository taskResultRepository;
    private final ResultRepository resultRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskResultService(TaskResultRepository taskResultRepository,
                             ResultRepository resultRepository,
                             TaskRepository taskRepository){
        this.taskResultRepository = taskResultRepository;
        this.resultRepository = resultRepository;
        this.taskRepository = taskRepository;
    }

    public void startTask(UUID resultId, UUID taskId) throws IllegalArgumentException {
        Result result = resultRepository.findOneWithChallenge(resultId);
        Task task = taskRepository.findOne(taskId);
        List<Task> tasks = result.getChallenge().getChallengeTemplate().getTasks();
        int taskOrder = tasks.indexOf(task);
        if (taskOrder == -1){
            throw new IllegalArgumentException("task.invalid");
        }
        if (taskOrder != 0){
            Task previousTask = tasks.get(taskOrder - 1);
            TaskResult previousTaskResult = taskResultRepository.findOneByTaskAndResult(previousTask.getId(), resultId);
            if (previousTaskResult == null || !previousTaskResult.isGreen()){
                throw  new AccessDeniedException("task.denied");
            }
        }

        TaskResult taskResult = taskResultRepository.findOneByTaskAndResult(taskId, resultId);
        if (taskResult != null){
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

    public void finishTaskResult(TaskResult taskResult, ZonedDateTime endTime, boolean green){
        taskResult.setEndTime(endTime);
        taskResult.setGreen(green);
        taskResultRepository.save(taskResult);

        Result result = taskResult.getKey().getResult();
        Task task = taskResult.getKey().getTask();

        if (result.getChallenge().getChallengeTemplate().getTasks().indexOf(task) == result.getChallenge().getChallengeTemplate().getTasks().size() - 1){
            result.setFinished(endTime);
            resultRepository.save(result);
        }
    }

    public TaskResult findByTaskAndResult(UUID taskId, UUID resultId){
        return taskResultRepository.findOneByTaskAndResult(taskId, resultId);
    }

}
