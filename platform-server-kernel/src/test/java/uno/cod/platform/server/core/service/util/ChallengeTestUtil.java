package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ChallengeTestUtil {
    public static Challenge getChallenge() {
        Challenge challenge = new Challenge(UUID.randomUUID(), "canonical-name", "name");
        challenge.setChallengeTemplate(ChallengeTemplateTestUtil.getChallengeTemplate());
        challenge.setStartDate(ZonedDateTime.now());
        challenge.setEndDate(ZonedDateTime.now());
        challenge.setInviteOnly(true);
        return challenge;
    }

    public static Challenge getChallenge(ChallengeCreateDto dto) {
        Challenge challenge = new Challenge(UUID.randomUUID(), dto.getCanonicalName(), dto.getName());
        challenge.setChallengeTemplate(ChallengeTemplateTestUtil.getChallengeTemplate(dto.getCanonicalName()));
        challenge.setInviteOnly(dto.isInviteOnly());
        challenge.setStartDate(dto.getStartDate());
        return challenge;
    }

    public static ChallengeCreateDto getChallengeCreateDto() {
        ChallengeCreateDto dto = new ChallengeCreateDto();
        dto.setName("name");
        dto.setCanonicalName("canonical-name");
        dto.setTemplateCanonicalName("template-canonical-name");
        dto.setInviteOnly(true);
        dto.setStartDate(ZonedDateTime.now());
        return dto;
    }
}
