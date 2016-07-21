package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskTestUtil {
    public static Task getValidTask() {
        Task task = new Task(UUID.randomUUID(), "canonicalName", "name");
        task.setDescription("description");
        task.setInstructions("instructions");
        task.setOrganization(OrganizationTestUtil.getOrganization());
        task.setLanguages(Collections.singleton(LanguageTestUtil.getLanguage()));
        task.setSkillMap(SkillMapTestUtil.getValidSkillMap());
        task.setDuration(Duration.ofMinutes(90));
        task.setEndpoint(EndpointTestUtil.getEndpoint());
        task.setRunner(RunnerTestUtil.getRunner());
        return task;
    }

    public static Task getTask(TaskCreateDto dto) {
        Task task = new Task(UUID.randomUUID(), dto.getCanonicalName(), dto.getName());
        task.setDescription(dto.getDescription());
        task.setInstructions(dto.getInstructions());
        task.setDuration(dto.getDuration());
        task.setSkillMap(dto.getSkillMap());
        task.setParams(dto.getParams());
        task.setRunner(RunnerTestUtil.getRunner(dto.getRunnerId()));
        task.setEndpoint(EndpointTestUtil.getEndpoint(dto.getEndpointId()));
        task.setOrganization(OrganizationTestUtil.getOrganization(dto.getOrganizationId()));
        return task;
    }

    public static TaskCreateDto getTaskCreateDto() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setName("name");
        dto.setDescription("description");
        dto.setInstructions("instructions");
        dto.setOrganizationId(UUID.randomUUID());
        dto.setSkillMap(SkillMapTestUtil.getValidSkillMap());
        dto.setDuration(Duration.ofMinutes(90));
        dto.setEndpointId(UUID.randomUUID());
        dto.setRunnerId(UUID.randomUUID());
        dto.setPublic(true);
        Map<String, String> params = new HashMap<>();
        params.put("test", "Test");
        dto.setParams(params);
        return dto;
    }
}
