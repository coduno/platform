package uno.cod.platform.server.core.dto.user;

import org.hibernate.validator.constraints.Email;

public class UserPasswordResetMailDto {
    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
