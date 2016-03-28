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
import java.util.UUID;

@Service
@Transactional
public class SubmissionService {
    private final SubmissionRepository repository;
    private final ResultRepository resultRepository;
    private final TaskRepository taskRepository;
    private final TestRepository testRepository;
    private final RuntimeClient runtimeClient;
    private final IClientPushConnection appClientConnection;
    private final PlatformStorage platformStorage;
    private final TaskResultService taskResultService;
    private final TestResultRepository testResultRepository;

    @Value("${coduno.storage.gcs.buckets.submissions}")
    private String bucket;

    @Autowired
    public SubmissionService(SubmissionRepository repository,
                             ResultRepository resultRepository,
                             TaskRepository taskRepository,
                             TestRepository testRepository,
                             PlatformStorage platformStorage,
                             RuntimeClient runtimeClient,
                             TaskResultService taskResultService,
                             TestResultRepository testResultRepository,
                             IClientPushConnection appClientConnection) {
        this.repository = repository;
        this.resultRepository = resultRepository;
        this.taskRepository = taskRepository;
        this.testRepository = testRepository;
        this.platformStorage = platformStorage;
        this.taskResultService = taskResultService;
        this.testResultRepository = testResultRepository;
        this.runtimeClient = runtimeClient;
        this.appClientConnection = appClientConnection;
    }

    public void create(User user, UUID resultId, UUID taskId, MultipartFile file, String language) throws IOException {
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

        TaskResult taskResult = taskResultService.findByTaskAndResult(taskId, resultId);

        Submission submission = new Submission();
        submission.setTaskResult(taskResult);
        submission.setFileName(file.getOriginalFilename());
        submission.setSubmissionTime(ZonedDateTime.now());
        submission = repository.save(submission);

        platformStorage.upload(bucket, submission.filePath(), file.getInputStream(), file.getContentType());

        repository.save(submission);

        // TODO(pbochis) update taskResult with results
        boolean green = true;
        for (Test test : task.getTests()) {
            green = green && runTest(user.getId(), submission, language, test);
        }

        submission.setGreen(green);
        repository.save(submission);
        if (green && !taskResult.isGreen()){
            taskResultService.finishTaskResult(taskResult, submission.getSubmissionTime(), green);
        }
    }

    public void run(User user, UUID taskId, MultipartFile file, String language) throws IOException {
        Task task = taskRepository.findOneWithRunner(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task.invalid");
        }
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("language", language);
        form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));
        if(task.getParams()!=null){
            for (Map.Entry<String, String> param : task.getParams().entrySet()) {
                form.add(param.getKey(), param.getValue());
            }
        }

        appClientConnection.send(user.getId(), runtimeClient.postToRuntime(task.getRunner().getName(), form).toString());
    }

    public boolean testOutput(UUID testId, MultipartFile file) throws IOException {
        Test test = testRepository.findOneWithRunner(testId);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));
        form.add("output_test", "true");
        // TODO quickfix for runtime fault
        form.add("language", "py");
        Map<String, String> params = test.getParams();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                form.add(param.getKey(), param.getValue());
            }
        }
        JsonNode obj = runtimeClient.postToRuntime(test.getRunner().getName(), form);
        if(obj.get("Failed")==null){
            return false;
        }
        return !obj.get("Failed").booleanValue();
    }

    private boolean runTest(UUID userId, Submission submission, String language, Test test) throws IOException {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("language", language);
        form.add("files_gcs", submission.filePath());
        Map<String, String> params = test.getParams();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                form.add(param.getKey(), param.getValue());
            }
        }
        JsonNode obj = runtimeClient.postToRuntime(test.getRunner().getName(), form);
        ((ObjectNode) obj).put("Test", test.getId().toString());

        boolean failed = (obj.get("Stderr") != null && !obj.get("Stderr").asText().isEmpty()) || obj.get("Failed").booleanValue();

        TestResult testResult = new TestResult();
        testResult.setTest(test);
        testResult.setSubmission(submission);
        testResult.setGreen(!failed);
        testResultRepository.save(testResult);
        //TODO: think wether we should save the test results in the database or as a json file in gcs
        appClientConnection.send(userId, obj.toString());

        return !failed;
    }
}
