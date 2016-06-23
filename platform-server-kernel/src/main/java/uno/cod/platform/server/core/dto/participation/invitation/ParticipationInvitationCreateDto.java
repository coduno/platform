package uno.cod.platform.server.core.dto.participation.invitation;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public class ParticipationInvitationCreateDto {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID challengeId;
    @NotNull
    private Set<String> emails;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(UUID challengeId) {
        this.challengeId = challengeId;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }
}
