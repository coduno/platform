package uno.cod.platform.server.core.dto.team.invitation;

import javax.validation.constraints.NotNull;

public class TeamInvitationCreateDto {
    @NotNull
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
