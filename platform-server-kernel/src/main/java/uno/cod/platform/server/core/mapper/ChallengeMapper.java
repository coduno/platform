package uno.cod.platform.server.core.mapper;

import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.dto.user.UserShowDto;

import java.util.stream.Collectors;

/**
 * Created by Paul on 3/22/2016.
 */
public class ChallengeMapper {

    public static ChallengeDto map(Challenge challenge){
        if (challenge == null){
            return null;
        }
        ChallengeDto dto = new ChallengeDto(challenge);
        if (challenge.getInvitedUsers() != null){
            dto.setInvitedUsers(challenge.getInvitedUsers().stream().map(UserShowDto::new).collect(Collectors.toList()));
        }
        return dto;
    }

}
