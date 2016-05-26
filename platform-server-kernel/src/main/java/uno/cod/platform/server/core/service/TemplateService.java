package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.repository.TaskRepository;
import uno.cod.storage.PlatformStorage;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * TemplateService is responsible for uploading new templates to backing
 * storage and adding a tuple (objectName, readableName) representing the
 * template to a task.
 * Furthermore it can translate a given task plus the objectName of a
 * template into a list of URLs that point at the files that are contained
 * by the template.
 * It abstracts choice of correct bucket and expiration duration.
 */
@Service
@Transactional
public class TemplateService {
    private final TaskRepository taskRepository;
    private final PlatformStorage storage;

    @Value("${coduno.template.bucket}")
    private String bucket;

    @Value("#{T(java.time.Duration).parse('${coduno.template.expire}')}")
    private Duration expiryDuration;

    @Autowired
    public TemplateService(TaskRepository taskRepository, PlatformStorage storage) {
        this.taskRepository = taskRepository;
        this.storage = storage;
    }

    public void save(UUID taskId, String objectName, String readableName, MultipartFile file) {
        if (file == null) {
            throw new CodunoIllegalArgumentException("file.invalid");
        }
        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            throw new CodunoIllegalArgumentException("task.invalid");
        }
        String path = task.getCanonicalName() + "/" + objectName + "/" + file.getOriginalFilename();
        try {
            storage.upload(bucket, path, file.getInputStream(), "text/plain");
        } catch (IOException e) {
            throw new CodunoIllegalArgumentException("file.invalid");
        }
        task.putTemplate(objectName, readableName);
        taskRepository.save(task);
    }

    public List<String> getTemplateUrls(UUID taskId, String objectName) throws GeneralSecurityException, IOException {
        Task task = taskRepository.getOne(taskId);

        if (task == null) {
            throw new CodunoNoSuchElementException("task.invalid");
        }

        List<String> urls = storage.exposeFilesInFolder(
                bucket,
                task.getCanonicalName() + "/" + objectName,
                (System.currentTimeMillis() / 1000) + expiryDuration.getSeconds()
        );

        if (urls.isEmpty()) {
            throw new CodunoNoSuchElementException("template.invalid");
        }

        return urls;
    }
}
