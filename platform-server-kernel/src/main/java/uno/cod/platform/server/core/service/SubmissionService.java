package uno.cod.platform.server.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.runtime.RuntimeClient;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.exception.CodunoAccessDeniedException;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.*;
import uno.cod.storage.PlatformStorage;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
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
    private final LanguageRepository languageRepository;
    private final IClientPushConnection appClientConnection;
    private final PlatformStorage platformStorage;
    private final TaskResultService taskResultService;
    private final TestResultRepository testResultRepository;

    @Value("${coduno.submission.bucket}")
    private String bucket;

    @Autowired
    public SubmissionService(SubmissionRepository repository,
                             ResultRepository resultRepository,
                             TaskRepository taskRepository,
                             TestRepository testRepository,
                             PlatformStorage platformStorage,
                             LanguageRepository languageRepository,
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
        this.languageRepository = languageRepository;
        this.testResultRepository = testResultRepository;
        this.runtimeClient = runtimeClient;
        this.appClientConnection = appClientConnection;
    }

    public void compileAndRun(UUID resultId, UUID taskId, MultipartFile[] files, String language) throws IOException {
        Submission submission = create(resultId, taskId);
        TaskResultKey key = submission.getTaskResult().getKey();

        submission.setLanguage(languageRepository.findOneByCanonicalName(language));

        for (MultipartFile file : files) {
            final String path = submission.filePath() + file.getOriginalFilename();
            platformStorage.upload(bucket, path, file.getInputStream(), file.getContentType());
        }

        submission = repository.save(submission);

        boolean successful = true;
        List<Test> tests = testRepository.findByTaskIdOrderByIndex(taskId);
        for (Test test : tests) {
            MultiValueMap<String, Object> form = createForm(test.getParams());
            form.add("language", language);
            for (MultipartFile file : files) {
                form.add("files", bucket + "/" + submission.filePath() + file.getOriginalFilename());
            }
            successful = runAndSendResults(form, getClientEndpoint(key.getResult()), submission, test) && successful;
        }

        if (successful) {
            success(submission);
        }
    }

    public void validateSolution(UUID resultId, UUID taskId, MultipartFile[] files) throws IOException {
        Submission submission = create(resultId, taskId);
        TaskResultKey key = submission.getTaskResult().getKey();

        boolean successful = true;

        // NOTE(flowlo): Traditionally, when validating solution files, one file is
        // validated at a time (separately). Therefore we're splitting up the uploaded
        // array of files here.
        for (MultipartFile file : files) {
            UUID testId = UUID.fromString(file.getOriginalFilename());
            Test test = testRepository.findOneWithRunner(testId);

            MultiValueMap<String, Object> form = createForm(test.getParams());
            form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));
            form.add("validate", "true");

            successful = runAndSendResults(form, getClientEndpoint(key.getResult()), submission, test) && successful;
        }
        successful = files.length == testRepository.findByTaskIdOrderByIndex(key.getTask().getId()).size() && successful;

        if (successful) {
            success(submission);
        }
    }

    private CanonicalEntity getClientEndpoint(Result result) {
        if (result.getTeam() != null) {
            return result.getTeam();
        }
        return result.getUser();
    }

    private Submission create(UUID resultId, UUID taskId) {
        Result result = resultRepository.findOne(resultId);
        if (result == null) {
            throw new CodunoIllegalArgumentException("result.invalid");
        }

        Challenge challenge = result.getChallenge();
        if (challenge.getEndDate() != null && challenge.getEndDate().isBefore(ZonedDateTime.now())) {
            throw new CodunoAccessDeniedException("challenge.ended");
        }

        Task task = taskRepository.findOneWithTests(taskId);
        if (task == null) {
            throw new CodunoIllegalArgumentException("task.invalid");
        }

        TaskResult taskResult = taskResultService.findByTaskAndResult(taskId, resultId);

        Submission submission = new Submission();
        submission.setTaskResult(taskResult);
        submission.setSubmissionTime(ZonedDateTime.now());
        return repository.save(submission);
    }

    private void success(Submission submission) {
        submission.setSuccessful(true);
        repository.save(submission);
        taskResultService.finishTaskResult(submission.getTaskResult(), submission.getSubmissionTime(), true);
        TaskResultKey key = submission.getTaskResult().getKey();
        appClientConnection.sendLevelCompleted(key.getResult().getUser(), key.getTask());
    }

    private MultiValueMap<String, Object> createForm(Map<String, String> params) {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        addParams(form, params);

        return form;
    }

    private void addParams(MultiValueMap<String, Object> form, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> param : params.entrySet()) {
            form.add(param.getKey(), param.getValue());
        }
    }

    private boolean runAndSendResults(MultiValueMap<String, Object> form, CanonicalEntity user, Submission submission, Test test) throws IOException {
        JsonNode obj = runtimeClient.postToRuntime(test.getRunner().getPath(), form);
        ((ObjectNode) obj).put("test", test.getId().toString());

        if (obj.get("failure") != null) {
            appClientConnection.send(user, obj.toString());
            return false;
        }

        boolean successful = obj.get("successful") != null && obj.get("successful").booleanValue();

        TestResult testResult = new TestResult();
        testResult.setTest(test);
        testResult.setSubmission(submission);
        testResult.setSuccessful(successful);
        testResultRepository.save(testResult);
        // TODO: Think whether we should save the test
        // results in the database or as a JSON file in GCS.
        appClientConnection.send(user, obj.toString());
        return successful;
    }

    public void run(User user, UUID taskId, MultipartFile[] files, String language) throws IOException {
        Task task = taskRepository.findOneWithRunner(taskId);
        if (task == null) {
            throw new CodunoIllegalArgumentException("task.invalid");
        }

        MultiValueMap<String, Object> form = createForm(task.getParams());
        form.add("language", language);

        for (MultipartFile file : files) {
            form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));
        }

        appClientConnection.send(user, runtimeClient.postToRuntime(task.getRunner().getPath(), form).toString());
    }
}
