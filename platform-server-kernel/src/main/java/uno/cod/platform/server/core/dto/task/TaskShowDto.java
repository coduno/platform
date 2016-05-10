package uno.cod.platform.server.core.dto.task;

import uno.cod.platform.server.core.domain.CodingSkill;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.assignment.AssignmentShowDto;
import uno.cod.platform.server.core.dto.language.LanguageShowDto;
import uno.cod.platform.server.core.dto.organization.OrganizationShowDto;
import uno.cod.platform.server.core.dto.runner.RunnerShowDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskShowDto extends AssignmentShowDto {
    private String canonicalName;
    private Map<String, String> templates;
    private Map<CodingSkill, Double> skillMap;
    private boolean normalRunAvailable;
    private List<LanguageShowDto> languages;
    private RunnerShowDto runner;
    private OrganizationShowDto organization;

    public TaskShowDto(Task task) {
        this.setId(task.getId());
        this.setName(task.getName());
        this.setCanonicalName(task.getCanonicalName());
        this.setDescription(task.getDescription());
        this.setInstructions(task.getInstructions());
        this.setDuration(task.getDuration());
        this.setSkillMap(task.getSkillMap());
        this.normalRunAvailable = task.getRunner() != null;
        if (task.getLanguages() != null) {
            this.languages = task.getLanguages().stream().map(LanguageShowDto::new).collect(Collectors.toList());
        }
        if (task.getRunner() != null) {
            this.runner = new RunnerShowDto(task.getRunner());
        }
        if (task.getOrganization() != null) {
            this.organization = new OrganizationShowDto(task.getOrganization());
        }
    }

    public Map<CodingSkill, Double> getSkillMap() {
        return skillMap;
    }

    public void setSkillMap(Map<CodingSkill, Double> skillMap) {
        this.skillMap = skillMap;
    }

    public boolean isNormalRunAvailable() {
        return normalRunAvailable;
    }

    public void setNormalRunAvailable(boolean normalRunAvailable) {
        this.normalRunAvailable = normalRunAvailable;
    }

    public Map<String, String> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }

    public List<LanguageShowDto> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageShowDto> languages) {
        this.languages = languages;
    }

    public void putTemplate(String objectName, String readableName) {
        if (templates == null) {
            templates = new HashMap<>();
        }
        templates.put(objectName, readableName);
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public RunnerShowDto getRunner() {
        return runner;
    }

    public void setRunner(RunnerShowDto runner) {
        this.runner = runner;
    }

    public OrganizationShowDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationShowDto organization) {
        this.organization = organization;
    }
}
