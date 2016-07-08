package uno.cod.platform.server.core.mapper;

import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.dto.location.LocationDetailShowDto;
import uno.cod.platform.server.core.dto.user.UserShortShowDto;

import java.util.stream.Collectors;

public class ChallengeMapper {
    public static ChallengeDto map(Challenge challenge) {
        if (challenge == null) {
            return null;
        }
        ChallengeDto dto = new ChallengeDto(challenge);
        if (challenge.getInvitedUsers() != null) {
            dto.setInvitedUsers(challenge.getInvitedUsers().stream().map(UserShortShowDto::new).collect(Collectors.toList()));
        }
        if (challenge.getLocationDetails() != null) {
            dto.setLocations(challenge.getLocationDetails().stream().map(LocationDetailShowDto::new).collect(Collectors.toList()));
        }
        return dto;
    }
}
