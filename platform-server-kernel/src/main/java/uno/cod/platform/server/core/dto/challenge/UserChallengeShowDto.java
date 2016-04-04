package uno.cod.platform.server.core.dto.challenge;

public class UserChallengeShowDto {
    private ChallengeDto challenge;
    private ChallengeStatus status;

    public UserChallengeShowDto() {
        this.status = ChallengeStatus.INVITED;
    }

    public ChallengeDto getChallenge() {
        return challenge;
    }

    public void setChallenge(ChallengeDto challenge) {
        this.challenge = challenge;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public enum ChallengeStatus {
        INVITED,
        IN_PROGRESS,
        COMPLETED;
    }
}
