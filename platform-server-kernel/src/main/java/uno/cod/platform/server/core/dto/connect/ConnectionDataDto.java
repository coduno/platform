package uno.cod.platform.server.core.dto.connect;

import org.springframework.social.connect.Connection;

import java.io.Serializable;


public class ConnectionDataDto implements Serializable {

    private String providerId;

    private String providerUserId;

    private String displayName;

    private String profileUrl;

    private String imageUrl;

    public ConnectionDataDto(Connection<?> connection) {
        this(connection.createData().getProviderId(),
                connection.createData().getProviderUserId(),
                connection.createData().getDisplayName(),
                connection.createData().getProfileUrl(),
                connection.createData().getImageUrl());
    }

    public ConnectionDataDto(String providerId, String providerUserId, String displayName, String profileUrl, String imageUrl) {
        this.providerId = providerId;
        this.providerUserId = providerUserId;
        this.displayName = displayName;
        this.profileUrl = profileUrl;
        this.imageUrl = imageUrl;
    }

    /**
     * The id of the provider the connection is associated with.
     * @return The id of the provider the connection is associated with.
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * The id of the provider user this connection is connected to.
     * @return The id of the provider user this connection is connected to.
     */
    public String getProviderUserId() {
        return providerUserId;
    }

    /**
     * A display name for the connection.
     * @return A display name for the connection.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * A link to the provider's user profile page.
     * @return A link to the provider's user profile page.
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * An image visualizing the connection.
     * @return An image visualizing the connection.
     */
    public String getImageUrl() {
        return imageUrl;
    }
}
