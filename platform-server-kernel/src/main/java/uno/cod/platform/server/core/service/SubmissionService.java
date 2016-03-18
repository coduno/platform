package uno.cod.platform.server.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.runtime.RuntimeClient;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.repository.*;
import uno.cod.storage.PlatformStorage;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;

@Service
@Transactional
public class SubmissionService {
    private final SubmissionRepository repository;
    private final ResultRepository resultRepository;
    private final TaskRepository taskRepository;
    private final TestRepository testRepository;
    private final IClientPushConnection appClientConnection;
    private final PlatformStorage platformStorage;

    @Value("${coduno.storage.gcs.buckets.submissions}")
    private String bucket;

    @Value("${coduno.runtime_url}")
    private String runtimeUrl;

    @Autowired
    public SubmissionService(SubmissionRepository repository,
                             ResultRepository resultRepository,
                             TaskRepository taskRepository,
                             TestRepository testRepository,
                             PlatformStorage platformStorage,
                             IClientPushConnection appClientConnection) {
        this.repository = repository;
        this.resultRepository = resultRepository;
        this.taskRepository = taskRepository;
        this.testRepository = testRepository;
        this.platformStorage = platformStorage;
        this.appClientConnection = appClientConnection;
    }

    public void create(User user, Long resultId, Long taskId, MultipartFile file, String language) throws IOException {
        Result result = resultRepository.findOne(resultId);
        if (result == null) {
            throw new IllegalArgumentException("result.invalid");
        }

        Challenge challenge = result.getChallenge();
        if (challenge.getEndDate() != null && challenge.getEndDate().isBefore(ZonedDateTime.now())){
            throw new AccessDeniedException("challenge.ended");
        }

        Task task = taskRepository.findOneWithTests(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task.invalid");
        }

        Submission submission = new Submission();
        result.addSubmission(submission);
        task.addSubmission(submission);
        submission.setFileName(file.getOriginalFilename());
        submission = repository.save(submission);

        platformStorage.upload(bucket, submission.filePath(), file.getInputStream(), file.getContentType());

        repository.save(submission);

        // TODO update submission with results
        for (Test test : task.getTests()) {
            runTest(user.getId(), submission.filePath(), language, test);
        }
    }

    public void run(User user, Long taskId, MultipartFile file, String language) throws IOException {
        Task task = taskRepository.findOneWithRunner(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task.invalid");
        }
        // TODO update submission with results
        run(user.getId(), file, language, task.getRunner());
    }

    public boolean testOutput(Long testId, MultipartFile file) throws IOException {
        Test test = testRepository.findOneWithRunner(testId);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));
        form.add("output_test", "true");
        Map<String, String> params = test.getParams();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                form.add(param.getKey(), param.getValue());
            }
        }
        JsonNode obj = RuntimeClient.postToRuntime(runtimeUrl, test.getRunner().getName(), form);
        return obj.get("Failed").booleanValue();
    }

    private void run(Long userId, MultipartFile file, String language, Runner runner) throws IOException {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("language", language);
        form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));

        appClientConnection.send(userId, RuntimeClient.postToRuntime(runtimeUrl, runner.getName(), form).toString());
    }

    private void runTest(Long userId, String filePath, String language, Test test) throws IOException {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("language", language);
        form.add("files_gcs", filePath);
        Map<String, String> params = test.getParams();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                form.add(param.getKey(), param.getValue());
            }
        }
        JsonNode obj = RuntimeClient.postToRuntime(runtimeUrl, test.getRunner().getName(), form);
        ((ObjectNode) obj).put("Test", test.getId());
        appClientConnection.send(userId, obj.toString());
    }
}
