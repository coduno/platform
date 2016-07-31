package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.TeamMember;
import uno.cod.platform.server.core.domain.User;

import java.util.UUID;

public class TeamTestUtil {
    public static Team getTeam() {
        return getTeamWithMember(null);
    }

    public static Team getTeamWithMember(User user) {
        Team team = new Team(UUID.randomUUID(), "name", "canonical-name");
        team.setEnabled(true);
        if (user != null) {
            team.addTeamMember(new TeamMember(team, user));
        }
        return team;
    }
}
