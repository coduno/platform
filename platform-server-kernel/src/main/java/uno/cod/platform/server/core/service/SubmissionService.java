package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.Submission;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.SubmissionRepository;
import uno.cod.platform.server.core.repository.TaskRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class SubmissionService {
    private final SubmissionRepository repository;
    private final ResultRepository resultRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public SubmissionService(SubmissionRepository repository, ResultRepository resultRepository, TaskRepository taskRepository) {
        this.repository = repository;
        this.resultRepository = resultRepository;
        this.taskRepository = taskRepository;
    }

    public void create(Long resultId, Long taskId, MultipartFile file) throws IOException {
        Result result = resultRepository.findOne(resultId);
        if(result == null){
            throw new IllegalArgumentException("result.invalid");
        }
        Task task = taskRepository.findOne(taskId);
        if(task == null){
            throw new IllegalArgumentException("task.invalid");
        }
        // TODO save files with the code and put a reference in the submission

        Submission submission = new Submission();
        result.addSubmission(submission);
        task.addSubmission(submission);
        repository.save(submission);
    }
}
