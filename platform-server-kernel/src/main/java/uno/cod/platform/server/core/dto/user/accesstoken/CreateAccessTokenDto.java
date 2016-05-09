package uno.cod.platform.server.core.dto.user.accesstoken;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateAccessTokenDto {
    @NotNull
    @Size(min = 4, max = 255)
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
