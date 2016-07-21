package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.ChallengeTemplate;

import java.time.Duration;
import java.util.UUID;

public class ChallengeTemplateTestUtil {
    public static ChallengeTemplate getChallengeTemplate() {
        return getChallengeTemplate(UUID.randomUUID());
    }

    public static ChallengeTemplate getChallengeTemplate(UUID id) {
        ChallengeTemplate template = new ChallengeTemplate(id, "canonical-name", "name");
        template.setDuration(Duration.ofMinutes(50));
        return template;
    }

    public static ChallengeTemplate getChallengeTemplate(String canonicalName) {
        ChallengeTemplate result = new ChallengeTemplate(UUID.randomUUID(), canonicalName, "name");
        result.setDuration(Duration.ofHours(2));
        return result;
    }
}
