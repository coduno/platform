package uno.cod.platform.server.core.dto.challenge;

import uno.cod.platform.server.core.util.constraints.CanonicalName;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

public class ChallengeUpdateDto {
    private UUID id;

    @NotNull
    private String name;
    @CanonicalName
    private String canonicalName;
    private boolean inviteOnly;
    private ZonedDateTime startDate;

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

    public boolean isInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
}
