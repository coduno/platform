package uno.cod.platform.server.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.SubmissionRepository;
import uno.cod.platform.server.core.repository.TaskRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.storage.PlatformStorage;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

@Service
@Transactional
public class SubmissionService {
    private final SubmissionRepository repository;
    private final ResultRepository resultRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final PlatformStorage platformStorage;
    private final ObjectMapper objectMapper;

    @Value("${coduno.storage.gcs.buckets.submissions}")
    private String bucket;

    @Value("${coduno.runtime_url}")
    private String runtimeUrl;

    @Autowired
    public SubmissionService(SubmissionRepository repository,
                             ResultRepository resultRepository,
                             TaskRepository taskRepository,
                             UserRepository userRepository,
                             PlatformStorage platformStorage,
                             WebSocketService webSocketService) {
        this.repository = repository;
        this.resultRepository = resultRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.platformStorage = platformStorage;
        this.webSocketService = webSocketService;
        this.objectMapper = new ObjectMapper();
    }

    public void create(User user, Long resultId, Long taskId, MultipartFile file, String language) throws IOException {
        Result result = resultRepository.findOne(resultId);
        if (result == null) {
            throw new IllegalArgumentException("result.invalid");
        }
        Task task = taskRepository.findOneWithTests(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task.invalid");
        }
        // TODO save files with the code and put a reference in the submission

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

    private void run(Long userId, MultipartFile file, String language, Runner runner) throws IOException {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("language", language);
        form.add("files", new FileMessageResource(file.getBytes(), file.getOriginalFilename()));

        webSocketService.send(userId, postToRuntime(runner, form).toString());
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
        JsonNode obj = postToRuntime(test.getRunner(), form);
        ((ObjectNode) obj).put("Test", test.getId());
        webSocketService.send(userId, obj.toString());
    }

    private JsonNode postToRuntime(Runner runner, MultiValueMap<String, Object> form) throws IOException {
        JsonNode obj;
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(runtimeUrl + "/" + runner.getName(), form, String.class);
            obj = objectMapper.readValue(response.getBody(), JsonNode.class);
        } catch (HttpClientErrorException e) {
            obj = objectMapper.createObjectNode();
            ((ObjectNode) obj).put("error", e.getResponseBodyAsString());
        }
        return obj;
    }

}
