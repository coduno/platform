package uno.cod.platform.server.core.dto.user;


import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserCreateDto {
    @NotNull
    @Size(min = 5, max = 40)
    @Pattern(regexp = "^[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*$")
    private String nick;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 6, max = 40)
    private String password;

    private String challengeCanonicalName;

    // Firstname and Lastname can be set later, but not when initially creating the account
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChallengeCanonicalName() {
        return challengeCanonicalName;
    }

    public void setChallengeCanonicalName(String challengeCanonicalName) {
        this.challengeCanonicalName = challengeCanonicalName;
    }
}
