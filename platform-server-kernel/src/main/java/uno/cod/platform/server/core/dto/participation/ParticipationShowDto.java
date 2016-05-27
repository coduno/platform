package uno.cod.platform.server.core.dto.participation;

import uno.cod.platform.server.core.domain.Participation;
import uno.cod.platform.server.core.dto.challenge.ChallengeShortShowDto;
import uno.cod.platform.server.core.dto.team.TeamShowDto;
import uno.cod.platform.server.core.dto.user.UserShowDto;

import java.util.Date;

public class ParticipationShowDto {
    private UserShowDto user;
    private TeamShowDto team;
    private ChallengeShortShowDto challenge;
    private Date created;

    public ParticipationShowDto(Participation participation) {
        if (participation.getKey().getUser() != null) {
            this.user = new UserShowDto(participation.getKey().getUser());
        }
        if (participation.getTeam() != null) {
            this.team = new TeamShowDto(participation.getTeam());
        }
        if (participation.getKey().getChallenge() != null) {
            this.challenge = new ChallengeShortShowDto(participation.getKey().getChallenge());
        }
        this.created = participation.getCreated();
    }

    public UserShowDto getUser() {
        return user;
    }

    public void setUser(UserShowDto user) {
        this.user = user;
    }

    public TeamShowDto getTeam() {
        return team;
    }

    public void setTeam(TeamShowDto team) {
        this.team = team;
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
}
