package uno.cod.platform.server.core.dto.challenge;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class ChallengeCreateDto {

    @NotNull
    private Long templateId;

    @NotNull
    private String name;
    private String canonicalName;
    private boolean inviteOnly;
    private ZonedDateTime startDate;

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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
