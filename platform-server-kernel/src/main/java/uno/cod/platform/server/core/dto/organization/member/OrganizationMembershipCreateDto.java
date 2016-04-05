package uno.cod.platform.server.core.dto.organization.member;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class OrganizationMembershipCreateDto {
    @NotNull
    private UUID userId;
    private boolean isAdmin;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

}
