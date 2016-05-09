package uno.cod.platform.server.core.dto.user.accesstoken;

public class CreatedAccessTokenDto {
    private String token;

    public CreatedAccessTokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
