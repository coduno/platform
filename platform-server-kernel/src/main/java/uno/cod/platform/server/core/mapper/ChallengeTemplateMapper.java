package uno.cod.platform.server.core.mapper;

import uno.cod.platform.server.core.domain.ChallengeTemplate;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.challenge.ChallengeShortShowDto;
import uno.cod.platform.server.core.dto.challenge.template.ChallengeTemplateShowDto;

import java.util.List;
import java.util.stream.Collectors;

public class ChallengeTemplateMapper {
    public static ChallengeTemplateShowDto map(ChallengeTemplate challengeTemplate) {
        if (challengeTemplate == null) {
            return null;
        }
        ChallengeTemplateShowDto dto = new ChallengeTemplateShowDto(challengeTemplate);
        dto.setEndpoint(EndpointMapper.map(challengeTemplate.getEndpoint()));
        dto.setTasks(challengeTemplate.getTasks().stream().map(Task::getId).collect(Collectors.toList()));
        dto.setChallenges(challengeTemplate.getChallenges().stream().map(ChallengeShortShowDto::new).collect(Collectors.toList()));
        return dto;
    }

    public static List<ChallengeTemplateShowDto> map(List<ChallengeTemplate> challengeTemplates) {
        return challengeTemplates.stream().map(ChallengeTemplateMapper::map).collect(Collectors.toList());
    }
}