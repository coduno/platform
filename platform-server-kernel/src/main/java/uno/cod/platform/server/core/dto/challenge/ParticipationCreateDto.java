package uno.cod.platform.server.core.dto.challenge;

import java.util.UUID;

public class ParticipationCreateDto {
    private String team;
    private UUID location;

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public UUID getLocation() {
        return location;
    }

    public void setLocation(UUID location) {
        this.location = location;
    }
}
