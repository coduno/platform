package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.core.domain.Language;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.domain.Template;
import uno.cod.platform.server.core.repository.LanguageRepository;
import uno.cod.platform.server.core.repository.TaskRepository;
import uno.cod.platform.server.core.repository.TemplateRepository;
import uno.cod.storage.PlatformStorage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.UUID;

@Service
public class TemplateService {
    private final TemplateRepository repository;
    private final TaskRepository taskRepository;
    private final LanguageRepository languageRepository;

    private final PlatformStorage storage;

    @Value("${coduno.storage.gcs.buckets.templates}")
    private String bucket;

    @Autowired
    public TemplateService(TemplateRepository repository, TaskRepository taskRepository, LanguageRepository languageRepository, PlatformStorage storage) {
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.languageRepository = languageRepository;
        this.storage = storage;
    }

    public void save(UUID taskId, UUID languageId, String path, MultipartFile file){
        Task task = taskRepository.findOne(taskId);
        if(task==null){
            throw new IllegalArgumentException("task.invalid");
        }
        Language language = languageRepository.findOne(languageId);
        if(language==null){
            throw new IllegalArgumentException("language.invalid");
        }
        try {
            storage.upload(bucket, path + "/" + file.getOriginalFilename(), file.getInputStream(), "text/plain");
        } catch (IOException e) {
            throw new IllegalArgumentException("file.invalid");
        }
        Template template = new Template();
        template.setFileName(path + "/" + file.getOriginalFilename());
        template.setTask(task);
        template.setLanguage(language);
        repository.save(template);
    }


    public String getTemplateUrl(UUID templateId) throws GeneralSecurityException, UnsupportedEncodingException {
        Template template = repository.findOne(templateId);
        //set expiration time to 2 hours
        Long expiration = (System.currentTimeMillis() / 1000) + 7200;
        return storage.exposeFile(bucket, template.filePath(), expiration);
    }
}
