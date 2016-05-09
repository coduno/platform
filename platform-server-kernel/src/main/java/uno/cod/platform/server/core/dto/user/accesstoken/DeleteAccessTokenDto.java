package uno.cod.platform.server.core.dto.user.accesstoken;


import java.util.UUID;

public class DeleteAccessTokenDto {
    private UUID id;

    public DeleteAccessTokenDto(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
