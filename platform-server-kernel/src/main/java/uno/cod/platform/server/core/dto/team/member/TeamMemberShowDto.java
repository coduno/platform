package uno.cod.platform.server.core.dto.team.member;

import uno.cod.platform.server.core.domain.TeamMember;
import uno.cod.platform.server.core.dto.user.UserShowDto;

public class TeamMemberShowDto extends UserShowDto {
    public TeamMemberShowDto(TeamMember member) {
        super(member.getKey().getUser());
        this.setAdmin(member.isAdmin());
    }
}
