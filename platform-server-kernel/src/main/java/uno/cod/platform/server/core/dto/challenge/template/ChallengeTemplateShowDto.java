package uno.cod.platform.server.core.dto.challenge.template;

import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.ChallengeTemplate;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.assignment.AssignmentShowDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeShortShowDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChallengeTemplateShowDto extends AssignmentShowDto {
    private String canonicalName;
    private List<UUID> tasks;
    private List<ChallengeShortShowDto> challenges;

    public ChallengeTemplateShowDto(ChallengeTemplate challengeTemplate) {
        BeanUtils.copyProperties(challengeTemplate, this);
        if (challengeTemplate.getChallenges() != null) {
            this.challenges = challengeTemplate.getChallenges().stream().map(ChallengeShortShowDto::new).collect(Collectors.toList());
        }
        if (challengeTemplate.getTasks() != null) {
            this.tasks = challengeTemplate.getTasks().stream().map(Task::getId).collect(Collectors.toList());
        }

        this.canonicalName = challengeTemplate.getCanonicalName();
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public List<ChallengeShortShowDto> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<ChallengeShortShowDto> challenges) {
        this.challenges = challenges;
    }

    public List<UUID> getTasks() {
        return tasks;
    }

    public void setTasks(List<UUID> tasks) {
        this.tasks = tasks;
    }
}
