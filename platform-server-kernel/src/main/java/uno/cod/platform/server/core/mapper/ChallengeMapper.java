package uno.cod.platform.server.core.mapper;

import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.dto.challenge.ChallengeShowDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;

import java.util.List;
import java.util.stream.Collectors;

public class ChallengeMapper {
    public static ChallengeShowDto map(Challenge challenge){
        if(challenge==null){
            return null;
        }
        ChallengeShowDto dto = new ChallengeShowDto(challenge);
        dto.setEndpoint(EndpointMapper.map(challenge.getEndpoint()));
        dto.setTasks(challenge.getTasks().stream().map(e -> TaskMapper.map(e)).collect(Collectors.toList()));
        return dto;
    }

    public static List<ChallengeShowDto> map(List<Challenge> challenges){
        return challenges.stream().map(e -> map(e)).collect(Collectors.toList());
    }
}
