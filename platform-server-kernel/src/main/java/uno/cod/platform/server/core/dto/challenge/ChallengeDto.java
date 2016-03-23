package uno.cod.platform.server.core.dto.challenge;

import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.dto.user.UserShowDto;

import java.time.ZonedDateTime;
import java.util.List;

public class ChallengeDto {
    private Long id;
    private String name;
    private String canonicalName;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private boolean inviteOnly;

    //ToDo: add List<ResultShortShowDto> after TaskResult entity is created so we can display
    // the last finished task like in the old catcoder view.

    private List<UserShowDto> invitedUsers;

    public ChallengeDto(Challenge challenge){
        BeanUtils.copyProperties(challenge, this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    public List<UserShowDto> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<UserShowDto> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }
}
