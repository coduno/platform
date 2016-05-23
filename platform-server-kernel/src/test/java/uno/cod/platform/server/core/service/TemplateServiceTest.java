package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import uno.cod.platform.server.core.domain.Template;
import uno.cod.platform.server.core.repository.LanguageRepository;
import uno.cod.platform.server.core.repository.TaskRepository;
import uno.cod.platform.server.core.repository.TemplateRepository;
import uno.cod.platform.server.core.service.util.LanguageTestUtil;
import uno.cod.platform.server.core.service.util.TaskTestUtil;
import uno.cod.storage.PlatformStorage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TemplateServiceTest {
    private TemplateService service;
    private TemplateRepository repository;
    private TaskRepository taskRepository;
    private LanguageRepository languageRepository;

    private PlatformStorage storage;

    @Before
    public void setUp() throws Exception {
        this.repository = Mockito.mock(TemplateRepository.class);
        this.taskRepository = Mockito.mock(TaskRepository.class);
        this.languageRepository = Mockito.mock(LanguageRepository.class);
        this.storage = Mockito.mock(PlatformStorage.class);

        this.service = new TemplateService(repository, taskRepository, languageRepository, storage);
    }

    @Test
    public void save() throws Exception {
        Template template = new Template();
        template.setId(UUID.randomUUID());
        template.setLanguage(LanguageTestUtil.getLanguage());
        template.setTask(TaskTestUtil.getValidTask());

        Mockito.when(taskRepository.findOne(template.getTask().getId())).thenReturn(template.getTask());
        Mockito.when(languageRepository.findOne(template.getLanguage().getId())).thenReturn(template.getLanguage());
        Mockito.doNothing().when(storage).upload(Mockito.anyString(), Mockito.anyString(), Mockito.any(InputStream.class), Mockito.anyString());

        service.save(template.getTask().getId(), template.getLanguage().getId(), new MockMultipartFile("filename", new byte[]{}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveNullFile() throws Exception {
        Template template = new Template();
        template.setId(UUID.randomUUID());
        template.setLanguage(LanguageTestUtil.getLanguage());
        template.setTask(TaskTestUtil.getValidTask());

        Mockito.when(taskRepository.findOne(template.getTask().getId())).thenReturn(template.getTask());
        Mockito.when(languageRepository.findOne(template.getLanguage().getId())).thenReturn(template.getLanguage());

        service.save(template.getTask().getId(), template.getLanguage().getId(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveInvalidFileForUpload() throws Exception {
        Template template = new Template();
        template.setId(UUID.randomUUID());
        template.setLanguage(LanguageTestUtil.getLanguage());
        template.setTask(TaskTestUtil.getValidTask());

        Mockito.when(taskRepository.findOne(template.getTask().getId())).thenReturn(template.getTask());
        Mockito.when(languageRepository.findOne(template.getLanguage().getId())).thenReturn(template.getLanguage());
        Mockito.doThrow(IOException.class).when(storage).upload(Mockito.anyString(), Mockito.anyString(), Mockito.any(InputStream.class), Mockito.anyString());

        service.save(template.getTask().getId(), template.getLanguage().getId(), new MockMultipartFile("filename", new byte[]{}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveTaskNotValid() throws Exception {
        Template template = new Template();
        template.setId(UUID.randomUUID());
        template.setLanguage(LanguageTestUtil.getLanguage());
        template.setTask(TaskTestUtil.getValidTask());

        Mockito.when(taskRepository.findOne(template.getTask().getId())).thenReturn(null);
        Mockito.when(languageRepository.findOne(template.getLanguage().getId())).thenReturn(template.getLanguage());

        service.save(template.getTask().getId(), template.getLanguage().getId(), new MockMultipartFile("filename", new byte[]{}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveLanguageNotValid() throws Exception {
        Template template = new Template();
        template.setId(UUID.randomUUID());
        template.setLanguage(LanguageTestUtil.getLanguage());
        template.setTask(TaskTestUtil.getValidTask());

        Mockito.when(taskRepository.findOne(template.getTask().getId())).thenReturn(template.getTask());
        Mockito.when(languageRepository.findOne(template.getLanguage().getId())).thenReturn(null);

        service.save(template.getTask().getId(), template.getLanguage().getId(), new MockMultipartFile("filename", new byte[]{}));
    }

    @Test
    public void getTemplateUrl() throws Exception {
        String url = "random-url";
        Template template = new Template();
        template.setId(UUID.randomUUID());
        template.setLanguage(LanguageTestUtil.getLanguage());
        template.setTask(TaskTestUtil.getValidTask());

        Mockito.when(repository.findOne(template.getId())).thenReturn(template);
        Mockito.when(storage.exposeFilesInFolder(Mockito.anyString(), Mockito.eq(template.filePath()), Mockito.anyLong())).thenReturn(Collections.singletonList(url));

        List<String> templateUrls = service.getTemplateUrl(template.getId());
        Assert.assertEquals(templateUrls.size(), 1);
        Assert.assertEquals(templateUrls.get(0), url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTemplateUrlNotExisting() throws Exception {
        UUID templateId = UUID.randomUUID();
        Mockito.when(repository.findOne(templateId)).thenReturn(null);

        service.getTemplateUrl(templateId);
    }
}