package uno.cod.platform.server.core.dto.task;

import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.CodingSkill;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.assignment.AssignmentShowDto;
import uno.cod.platform.server.core.dto.language.LanguageShowDto;
import uno.cod.platform.server.core.dto.template.TemplateShowDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskShowDto extends AssignmentShowDto{
    public TaskShowDto(Task task){
        BeanUtils.copyProperties(task, this);
        this.normalRunAvailable = task.getRunner() != null;
        if(task.getLanguages()!=null){
            this.languages = task.getLanguages().stream().map(LanguageShowDto::new).collect(Collectors.toList());
        }
    }

    private List<TemplateShowDto> templates;
    private Map<CodingSkill, Double> skillMap;
    private boolean normalRunAvailable;
    private List<LanguageShowDto> languages;

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

    public List<TemplateShowDto> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplateShowDto> templates) {
        this.templates = templates;
    }

    public List<LanguageShowDto> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageShowDto> languages) {
        this.languages = languages;
    }

    public void addTemplate(TemplateShowDto dto){
        if (templates == null){
            templates = new ArrayList<>();
        }
        templates.add(dto);
    }
}
