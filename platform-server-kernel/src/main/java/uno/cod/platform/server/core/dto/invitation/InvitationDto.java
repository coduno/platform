package uno.cod.platform.server.core.dto.invitation;

import javax.validation.constraints.NotNull;

public class InvitationDto {
    @NotNull
    private String email;

    @NotNull
    private String canonicalName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
