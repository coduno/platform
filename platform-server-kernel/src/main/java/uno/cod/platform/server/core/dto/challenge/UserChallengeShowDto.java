package uno.cod.platform.server.core.dto.challenge;

import uno.cod.platform.server.core.dto.location.LocationDetailShowDto;

public class UserChallengeShowDto {
    private ChallengeDto challenge;
    private ChallengeStatus status;
    private String registeredAs;
    private LocationDetailShowDto location;

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

    public LocationDetailShowDto getLocation() {
        return location;
    }

    public void setLocation(LocationDetailShowDto location) {
        this.location = location;
    }

    public enum ChallengeStatus {
        INVITED(0),
        REGISTERED(1),
        IN_PROGRESS(2),
        OPEN(3),
        INVITE_ONLY(4),
        COMPLETED(5),
        ENDED(6);

        private final int value;

        ChallengeStatus(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
