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
import java.util.*;

@Service
@Transactional
public class SubmissionService {
    private static final Integer MAX_RESPONSE_SIZE = 2000000;
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

    @Value("${coduno.storage.gcs.buckets.submissions}")
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

    public enum SubmissionType {
        NORMAL,
        OUTPUT;
    }

    public void submitToRuntime(SubmissionType type, UUID resultId, UUID taskId, MultipartFile[] files, String language) throws IOException {
        Result result = resultRepository.findOne(resultId);
        if (result == null) {
            throw new IllegalArgumentException("result.invalid");
        }

        Challenge challenge = result.getChallenge();
        if (challenge.getEndDate() != null && challenge.getEndDate().isBefore(ZonedDateTime.now())) {
            throw new AccessDeniedException("challenge.ended");
        }

        Task task = taskRepository.findOneWithTests(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task.invalid");
        }

        TaskResult taskResult = taskResultService.findByTaskAndResult(taskId, resultId);

        Submission submission = new Submission();
        submission.setTaskResult(taskResult);
        if (SubmissionType.NORMAL.equals(type)) {
            submission.setLanguage(languageRepository.findByTag(language));
            platformStorage.upload(bucket, submission.filePath(), files[0].getInputStream(), files[0].getContentType());
            submission.setFileName(files[0].getOriginalFilename());
        }
        submission.setSubmissionTime(ZonedDateTime.now());
        submission = repository.save(submission);
        repository.save(submission);
        boolean green = true;
        switch (type) {
            case NORMAL:
                for (Test test : task.getTests()) {
                    MultiValueMap<String, Object> form = createForm(test);
                    form.add("language", language);
                    form.add("files_gcs", submission.filePath());
                    green = green && runAndSendResults(form, result.getUser().getId(), submission, test);
                }
                break;
            case OUTPUT:
                for (MultipartFile file : files) {
                    UUID testId = UUID.fromString(file.getOriginalFilename());
                    Test test = testRepository.findOneWithRunner(testId);

                    MultiValueMap<String, Object> form = createForm(test);
                    form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));
                    form.add("validate", "true");

                    green = green && runAndSendResults(form, result.getUser().getId(), submission, test);
                }
                green = green && files.length == testRepository.findByTask(task.getId()).size();
                break;
        }

        if (green) {
            submission.setGreen(true);
            repository.save(submission);
            taskResultService.finishTaskResult(taskResult, submission.getSubmissionTime(), true);
        }
    }

    private MultiValueMap<String, Object> createForm(Test test){
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();

        Map<String, String> params = test.getParams();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                form.add(param.getKey(), param.getValue());
            }
        }

        return form;

    }

    private boolean runAndSendResults(MultiValueMap<String, Object> form , UUID userId, Submission submission, Test test) throws IOException {
        JsonNode obj = runtimeClient.postToRuntime(test.getRunner().getName(), form);
        ((ObjectNode) obj).put("test", test.getId().toString());

        if (obj.get("error") != null) {
            appClientConnection.send(userId, obj.toString());
            return false;
        }

        boolean successful = obj.get("successful") != null && !obj.get("successful").booleanValue();

        TestResult testResult = new TestResult();
        testResult.setTest(test);
        testResult.setSubmission(submission);
        testResult.setGreen(successful);
        testResultRepository.save(testResult);
        //TODO: think wether we should save the test results in the database or as a json file in gcs
        appClientConnection.send(userId, obj.toString());
        return successful;
    }

    public void run(User user, UUID taskId, MultipartFile file, String language) throws IOException {
        Task task = taskRepository.findOneWithRunner(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task.invalid");
        }
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("language", language);
        form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));
        if (task.getParams() != null) {
            for (Map.Entry<String, String> param : task.getParams().entrySet()) {
                form.add(param.getKey(), param.getValue());
            }
        }

        appClientConnection.send(user.getId(), runtimeClient.postToRuntime(task.getRunner().getName(), form).toString());
    }
}
