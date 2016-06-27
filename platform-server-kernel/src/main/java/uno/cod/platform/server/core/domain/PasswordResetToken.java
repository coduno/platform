package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
    @Id
    @Size(min = 26, max = 26)
    private String token;

    @OneToOne(optional = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime expire;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ZonedDateTime getExpire() {
        return expire;
    }

    public void setExpire(ZonedDateTime expires) {
        this.expire = expires;
    }
}
