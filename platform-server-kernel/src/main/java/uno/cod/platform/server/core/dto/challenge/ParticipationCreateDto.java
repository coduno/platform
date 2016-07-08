package uno.cod.platform.server.core.dto.challenge;

public class ParticipationCreateDto {
    private String team;

    /**
     * Maps to {@link uno.cod.platform.server.core.domain.Location#id}.
     */
    private String location;

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
