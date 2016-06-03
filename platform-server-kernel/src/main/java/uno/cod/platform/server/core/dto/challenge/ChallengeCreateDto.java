package uno.cod.platform.server.core.dto.challenge;

import uno.cod.platform.server.core.dto.location.LocationCreateDto;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class ChallengeCreateDto {

    @NotNull
    private UUID templateId;

    @NotNull
    private String name;
    private String canonicalName;
    private boolean inviteOnly;
    private ZonedDateTime startDate;

    private List<LocationCreateDto> locations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public List<LocationCreateDto> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationCreateDto> locations) {
        this.locations = locations;
    }
}
