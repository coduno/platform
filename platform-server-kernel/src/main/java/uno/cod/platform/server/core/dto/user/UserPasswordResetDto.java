package uno.cod.platform.server.core.dto.user;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

public class UserPasswordResetDto {
    @NotEmpty
    @Size(min = 6, max = 40)
    private String password;
    @NotEmpty
    @Size(min = 26, max = 26)
    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
