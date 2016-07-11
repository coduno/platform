package uno.cod.platform.server.core.dto.challenge;

import uno.cod.platform.server.core.dto.location.LocationUpdateDto;
import uno.cod.platform.server.core.util.constraints.CanonicalName;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Set;

public class ChallengeCreateDto {

    @CanonicalName
    private String templateCanonicalName;

    @NotNull
    private String name;
    @CanonicalName
    private String canonicalName;
    private boolean inviteOnly;
    private ZonedDateTime startDate;

    private Set<LocationUpdateDto> locations;

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

    public String getTemplateCanonicalName() {
        return templateCanonicalName;
    }

    public void setTemplateCanonicalName(String templateCanonicalName) {
        this.templateCanonicalName = templateCanonicalName;
    }

    public Set<LocationUpdateDto> getLocations() {
        return locations;
    }

    public void setLocations(Set<LocationUpdateDto> locations) {
        this.locations = locations;
    }
}
