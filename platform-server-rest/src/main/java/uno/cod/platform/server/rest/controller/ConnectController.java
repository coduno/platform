package uno.cod.platform.server.rest.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.*;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import uno.cod.platform.server.core.dto.connect.ConnectionDataDto;
import uno.cod.platform.server.core.dto.connect.ConnectionDto;
import uno.cod.platform.server.rest.RestUrls;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic UI controller for managing the account-to-service-provider connection flow.
 * <ul>
 * <li>GET /connect/{providerId}  - Get a web page showing connection status to {providerId}.</li>
 * <li>POST /connect/{providerId} - Initiate an connection with {providerId}.</li>
 * <li>GET /connect/{providerId}?oauth_verifier||code - Receive {providerId} authorization callback and establish the connection.</li>
 * <li>DELETE /connect/{providerId} - Disconnect from {providerId}.</li>
 * </ul>
 */
@RestController
public class ConnectController implements InitializingBean {

    private final static Log LOGGER = LogFactory.getLog(ConnectController.class);

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final ConnectionRepository connectionRepository;

    private final MultiValueMap<Class<?>, ConnectInterceptor<?>> connectInterceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();

    private final MultiValueMap<Class<?>, DisconnectInterceptor<?>> disconnectInterceptors = new LinkedMultiValueMap<Class<?>, DisconnectInterceptor<?>>();

    private ConnectSupport connectSupport;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    private String applicationUrl;

    @Value("${coduno.url}")
    private String frontendApplicationUrl;

    /**
     * Constructs a ConnectController.
     * @param connectionFactoryLocator the locator for {@link ConnectionFactory} instances needed to establish connections
     * @param connectionRepository the current user's {@link ConnectionRepository} needed to persist connections; must be a proxy to a request-scoped bean
     */
    @Inject
    public ConnectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.connectionRepository = connectionRepository;
    }

    /**
     * Configure the list of connect interceptors that should receive callbacks during the connection process.
     * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
     * @param interceptors the connect interceptors to add
     */
    public void setConnectInterceptors(List<ConnectInterceptor<?>> interceptors) {
        for (ConnectInterceptor<?> interceptor : interceptors) {
            addInterceptor(interceptor);
        }
    }

    /**
     * Configure the list of discconnect interceptors that should receive callbacks when connections are removed.
     * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
     * @param interceptors the connect interceptors to add
     */
    public void setDisconnectInterceptors(List<DisconnectInterceptor<?>> interceptors) {
        for (DisconnectInterceptor<?> interceptor : interceptors) {
            addDisconnectInterceptor(interceptor);
        }
    }

    /**
     * Configures the base secure URL for the application this controller is being used in e.g. <code>https://myapp.com</code>. Defaults to null.
     * If specified, will be used to generate OAuth callback URLs.
     * If not specified, OAuth callback URLs are generated from web request info.
     * You may wish to set this property if requests into your application flow through a proxy to your application server.
     * In this case, the request URI may contain a scheme, host, and/or port value that points to an internal server not appropriate for an external callback URL.
     * If you have this problem, you can set this property to the base external URL for your application and it will be used to construct the callback URL instead.
     * @param applicationUrl the application URL value
     */
    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    /**
     * Sets a strategy to use when persisting information that is to survive past the boundaries of a request.
     * The default strategy is to set the data as attributes in the HTTP Session.
     * @param sessionStrategy the session strategy.
     */
    public void setSessionStrategy(SessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    /**
     * Adds a ConnectInterceptor to receive callbacks during the connection process.
     * Useful for programmatic configuration.
     * @param interceptor the connect interceptor to add
     */
    public void addInterceptor(ConnectInterceptor<?> interceptor) {
        Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), ConnectInterceptor.class);
        connectInterceptors.add(serviceApiType, interceptor);
    }

    /**
     * Adds a DisconnectInterceptor to receive callbacks during the disconnection process.
     * Useful for programmatic configuration.
     * @param interceptor the connect interceptor to add
     */
    public void addDisconnectInterceptor(DisconnectInterceptor<?> interceptor) {
        Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), DisconnectInterceptor.class);
        disconnectInterceptors.add(serviceApiType, interceptor);
    }

    /**
     * Return the status of connections across all providers to the user.
     */
    @RequestMapping(value = RestUrls.CONNECT, method = RequestMethod.GET)
    public ConnectionDto connectionStatus() {
        ConnectionDto connectionDto = new ConnectionDto();
        connectionDto.setProviders(connectionFactoryLocator.registeredProviderIds());
        connectionDto.setConnections(connectionRepository.findAllConnections());
        return connectionDto;
    }

    /**
     * Return the status of the connections to the service provider to the user.
     * @param providerId the ID of the provider to show connection status
     */
    @RequestMapping(value = RestUrls.CONNECT_PROVIDERID, method = RequestMethod.GET)
    public List<ConnectionDataDto> connectionStatus(@PathVariable String providerId) {
        List<Connection<?>> connections = connectionRepository.findConnections(providerId);
        return connections.stream().map(ConnectionDataDto::new).collect(Collectors.toList());
    }

    /**
     * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
     * For OAuth1, fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
     * For OAuth2, redirects the user to the provider's site for authorization.
     * @param providerId the provider ID to connect to
     * @param request the request
     * @return a RedirectView to the provider's authorization page or to the connection status page if there is an error
     */
    @RequestMapping(value = RestUrls.CONNECT_PROVIDERID, method = RequestMethod.POST)
    public void connect(@PathVariable String providerId, NativeWebRequest request) throws IOException {
        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        preConnect(connectionFactory, parameters, request);

        ((HttpServletResponse) request.getNativeResponse()).sendRedirect(connectSupport.buildOAuthUrl(connectionFactory, request, parameters));
    }

    /**
     * Process the authorization callback from an OAuth 1 service provider.
     * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
     * On authorization verification, connects the user's local account to the account they hold at the service provider
     * Removes the request token from the session since it is no longer valid after the connection is established.
     * @param providerId the provider ID to connect to
     * @param request the request
     * @return a RedirectView to the connection status page
     */
    @RequestMapping(value = RestUrls.CONNECT_PROVIDERID, method = RequestMethod.GET, params = "oauth_token")
    public void oauth1Callback(@PathVariable String providerId, NativeWebRequest request) throws IOException {
        try {
            OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
            Connection<?> connection = connectSupport.completeConnection(connectionFactory, request);
            addConnection(connection, connectionFactory, request);
        } catch (Exception e) {
            LOGGER.warn("Exception while handling OAuth1 callback (" + e.getMessage() + "). Redirecting to " + providerId + " connection status page.");
        }

        ((HttpServletResponse) request.getNativeResponse()).sendRedirect(frontendApplicationUrl);
    }

    /**
     * Process the authorization callback from an OAuth 2 service provider.
     * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
     * On authorization verification, connects the user's local account to the account they hold at the service provider.
     * @param providerId the provider ID to connect to
     * @param request the request
     * @return a RedirectView to the connection status page
     */
    @RequestMapping(value = RestUrls.CONNECT_PROVIDERID, method = RequestMethod.GET, params = "code")
    public void oauth2Callback(@PathVariable String providerId, NativeWebRequest request) throws IOException {
        try {
            OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
            Connection<?> connection = connectSupport.completeConnection(connectionFactory, request);
            addConnection(connection, connectionFactory, request);
        } catch (Exception e) {
            LOGGER.warn("Exception while handling OAuth2 callback (" + e.getMessage() + "). Redirecting to " + providerId + " connection status page.");
        }

        ((HttpServletResponse) request.getNativeResponse()).sendRedirect(frontendApplicationUrl);
    }

    /**
     * Process an error callback from an OAuth 2 authorization as described at http://tools.ietf.org/html/rfc6749#section-4.1.2.1.
     * Called after upon redirect from an OAuth 2 provider when there is some sort of error during authorization, typically because the user denied authorization.
     * @param providerId the provider ID that the connection was attempted for
     * @param error the error parameter sent from the provider
     * @param errorDescription the error_description parameter sent from the provider
     * @param errorUri the error_uri parameter sent from the provider
     * @return a RedirectView to the connection status page
     */
    @RequestMapping(value = RestUrls.CONNECT_PROVIDERID, method = RequestMethod.GET, params = "error")
    public ResponseEntity oauth2ErrorCallback(@PathVariable String providerId,
                                            @RequestParam("error") String error,
                                            @RequestParam(value = "error_description", required = false) String errorDescription,
                                            @RequestParam(value = "error_uri", required = false) String errorUri) {
        LOGGER.warn("Exception while handling OAuth2 callback (Error: " + error + ", URI: " + errorUri + ", Desc: " + errorDescription + ").");
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Remove all provider connections for a user account.
     * The user has decided they no longer wish to use the service provider from this application.
     * Note: requires {@link HiddenHttpMethodFilter} to be registered with the '_method' request parameter set to 'DELETE' to convert web browser POSTs to DELETE requests.
     * @param providerId the provider ID to remove the connections for
     * @param request the request
     * @return a RedirectView to the connection status page
     */
    @RequestMapping(value = RestUrls.CONNECT_PROVIDERID, method = RequestMethod.DELETE)
    public ResponseEntity removeConnections(@PathVariable String providerId, NativeWebRequest request) {
        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
        preDisconnect(connectionFactory, request);
        connectionRepository.removeConnections(providerId);
        postDisconnect(connectionFactory, request);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Remove a single provider connection associated with a user account.
     * The user has decided they no longer wish to use the service provider account from this application.
     * Note: requires {@link HiddenHttpMethodFilter} to be registered with the '_method' request parameter set to 'DELETE' to convert web browser POSTs to DELETE requests.
     * @param providerId the provider ID to remove connections for
     * @param providerUserId the user's ID at the provider
     * @param request the request
     * @return a RedirectView to the connection status page
     */
    @RequestMapping(value = RestUrls.CONNECT_PROVIDERID_USERID, method = RequestMethod.DELETE)
    public ResponseEntity removeConnection(@PathVariable String providerId, @PathVariable String providerUserId, NativeWebRequest request) {
        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
        preDisconnect(connectionFactory, request);
        connectionRepository.removeConnection(new ConnectionKey(providerId, providerUserId));
        postDisconnect(connectionFactory, request);
        return new ResponseEntity(HttpStatus.OK);
    }

    // From InitializingBean
    public void afterPropertiesSet() throws Exception {
        this.connectSupport = new ConnectSupport(sessionStrategy);
        if (applicationUrl != null) {
            this.connectSupport.setApplicationUrl(applicationUrl);
        }
    }

    private void addConnection(Connection<?> connection, ConnectionFactory<?> connectionFactory, WebRequest request) throws DuplicateConnectionException {
        connectionRepository.addConnection(connection);
        postConnect(connectionFactory, connection, request);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void preConnect(ConnectionFactory<?> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {
        for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
            interceptor.preConnect(connectionFactory, parameters, request);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void postConnect(ConnectionFactory<?> connectionFactory, Connection<?> connection, WebRequest request) {
        for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
            interceptor.postConnect(connection, request);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void preDisconnect(ConnectionFactory<?> connectionFactory, WebRequest request) {
        for (DisconnectInterceptor interceptor : interceptingDisconnectionsTo(connectionFactory)) {
            interceptor.preDisconnect(connectionFactory, request);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void postDisconnect(ConnectionFactory<?> connectionFactory, WebRequest request) {
        for (DisconnectInterceptor interceptor : interceptingDisconnectionsTo(connectionFactory)) {
            interceptor.postDisconnect(connectionFactory, request);
        }
    }

    private List<ConnectInterceptor<?>> interceptingConnectionsTo(ConnectionFactory<?> connectionFactory) {
        Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
        List<ConnectInterceptor<?>> typedInterceptors = connectInterceptors.get(serviceType);
        if (typedInterceptors == null) {
            typedInterceptors = Collections.emptyList();
        }
        return typedInterceptors;
    }

    private List<DisconnectInterceptor<?>> interceptingDisconnectionsTo(ConnectionFactory<?> connectionFactory) {
        Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
        List<DisconnectInterceptor<?>> typedInterceptors = disconnectInterceptors.get(serviceType);
        if (typedInterceptors == null) {
            typedInterceptors = Collections.emptyList();
        }
        return typedInterceptors;
    }
}
