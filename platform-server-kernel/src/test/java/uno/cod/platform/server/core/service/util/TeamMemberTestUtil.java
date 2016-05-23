package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.TeamMember;
import uno.cod.platform.server.core.domain.TeamUserKey;
import uno.cod.platform.server.core.domain.User;

public class TeamMemberTestUtil {
    public static TeamMember getTeamMember(Team team, User user) {
        TeamMember member = new TeamMember();
        TeamUserKey key = new TeamUserKey();
        key.setUser(user);
        key.setTeam(team);
        member.setKey(key);
        return member;
    }
}
