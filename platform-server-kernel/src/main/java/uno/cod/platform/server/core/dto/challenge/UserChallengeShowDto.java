package uno.cod.platform.server.core.dto.challenge;

public class UserChallengeShowDto {
    private ChallengeDto challenge;
    private ChallengeStatus status;
    private String registeredAs;

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

    public String getRegisteredAs() {
        return registeredAs;
    }

    public void setRegisteredAs(String registeredAs) {
        this.registeredAs = registeredAs;
    }

    public enum ChallengeStatus {
        INVITED,
        IN_PROGRESS,
        ENDED,
        REGISTERED,
        OPEN,
        INVITE_ONLY,
        COMPLETED;
    }
}
