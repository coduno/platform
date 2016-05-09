package uno.cod.platform.server.core.dto.user.accesstoken;

import uno.cod.platform.server.core.domain.AccessToken;

import java.time.ZonedDateTime;
import java.util.UUID;

public class AccessTokenDto {
    private UUID id;
    private String comment;
    private ZonedDateTime created;
    private ZonedDateTime lastUsed;

    public AccessTokenDto(AccessToken accessToken) {
        this.id = accessToken.getId();
        this.comment = accessToken.getComment();
        this.created = accessToken.getCreated();
        this.lastUsed = accessToken.getLastUsed();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(ZonedDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
}
