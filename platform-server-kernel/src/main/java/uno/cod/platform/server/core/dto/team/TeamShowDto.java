package uno.cod.platform.server.core.dto.team;

import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.dto.team.member.TeamMemberShowDto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamShowDto {
    private UUID id;
    private String name;
    private String canonicalName;
    private Set<TeamMemberShowDto> members;

    public TeamShowDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.canonicalName = team.getCanonicalName();
        if (team.getMembers() == null) {
            return;
        }
        this.members = team.getMembers().stream().map(TeamMemberShowDto::new).collect(Collectors.toSet());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public Set<TeamMemberShowDto> getMembers() {
        return members;
    }

    public void setMembers(Set<TeamMemberShowDto> members) {
        this.members = members;
    }
}
