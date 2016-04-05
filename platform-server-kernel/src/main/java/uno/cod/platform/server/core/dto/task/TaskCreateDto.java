package uno.cod.platform.server.core.dto.task;

import org.hibernate.validator.constraints.NotEmpty;
import uno.cod.platform.server.core.domain.CodingSkill;
import uno.cod.platform.server.core.dto.assignment.AssignmentCreateDto;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

// TODO add languages and templates
public class TaskCreateDto extends AssignmentCreateDto{
    private boolean isPublic;

    @NotNull
    @NotEmpty
    private String canonicalName;

    @NotNull
    @NotEmpty
    private Map<CodingSkill, Double> skillMap;
    private Map<String, String> params;

    private UUID runnerId;

    public UUID getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(UUID runnerId) {
        this.runnerId = runnerId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Map<CodingSkill, Double> getSkillMap() {
        return skillMap;
    }

    public void setSkillMap(Map<CodingSkill, Double> skillMap) {
        this.skillMap = skillMap;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
