package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Access Tokens are used to provide applications,
 * other than our webfrontend, a possibility to authenticate
 * themselves without storing a (shared) password.
 */
@Entity
@Table(name = "access_token")
@NamedEntityGraph(name = "AccessToken.user.details",
        attributeNodes = {
                @NamedAttributeNode(value = "user", subgraph = "user")
        },
        subgraphs = @NamedSubgraph(name = "user",
                attributeNodes = {
                    @NamedAttributeNode("organizationMemberships"),
                    @NamedAttributeNode("teams"),
                    @NamedAttributeNode("invitedChallenges")
                }
        )
)
public class AccessToken extends IdentifiableEntity {
    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String token;

    private String comment;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    private ZonedDateTime lastUsed;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    protected void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(ZonedDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccessToken that = (AccessToken) o;

        return token != null ? token.equals(that.token) : that.token == null;
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }
}
