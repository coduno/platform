package uno.cod.platform.server.core.dto.participation;

import uno.cod.platform.server.core.domain.LocationDetail;
import uno.cod.platform.server.core.domain.Participation;
import uno.cod.platform.server.core.dto.challenge.ChallengeShortShowDto;
import uno.cod.platform.server.core.dto.location.LocationDetailShowDto;

import java.util.Date;

public class ParticipationShowDto {
    private String username;
    private String email;
    private String teamName;
    private String email;
    private String teamCanonicalName;
    private ChallengeShortShowDto challenge;
    private Date created;
    private LocationDetailShowDto location;

    public ParticipationShowDto(Participation participation) {
        if (participation.getKey().getUser() != null) {
            this.username = participation.getKey().getUser().getUsername();
            this.email = participation.getKey().getUser().getEmail();
        }
        if (participation.getTeam() != null) {
            this.teamName = participation.getTeam().getName();
            this.teamCanonicalName = participation.getTeam().getCanonicalName();
        }
        if (participation.getKey().getChallenge() != null) {
            this.challenge = new ChallengeShortShowDto(participation.getKey().getChallenge());
        }
        if (participation.getLocation() != null) {
            for (LocationDetail locationDetail : participation.getKey().getChallenge().getLocationDetails()) {
                if (!locationDetail.getKey().getLocation().getId().equals(participation.getLocation().getId())) {
                    continue;
                }
                this.location = new LocationDetailShowDto(locationDetail);
                break;
            }
        }
        this.created = participation.getCreated();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public ChallengeShortShowDto getChallenge() {
        return challenge;
    }

    public void setChallenge(ChallengeShortShowDto challenge) {
        this.challenge = challenge;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTeamCanonicalName() {
        return teamCanonicalName;
    }

    public void setTeamCanonicalName(String teamCanonicalName) {
        this.teamCanonicalName = teamCanonicalName;
    }

    public LocationDetailShowDto getLocation() {
        return location;
    }

    public void setLocation(LocationDetailShowDto location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
