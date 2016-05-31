package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.dto.challenge.ParticipationCreateDto;

public class ParticipationUtil {
    public static ParticipationCreateDto getCreateDto(String teamName) {
        ParticipationCreateDto dto = new ParticipationCreateDto();
        dto.setTeam(teamName);
        return dto;
    }
}
