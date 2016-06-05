package uno.cod.platform.server.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.connect.web.DisconnectInterceptor;
import org.springframework.util.CollectionUtils;
import uno.cod.platform.server.rest.controller.ConnectController;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class SocialWebConfig {
    @Autowired
    DataSource dataSource;

    @Autowired(required = false)
    private List<ConnectInterceptor<?>> connectInterceptors;

    @Autowired(required = false)
    private List<DisconnectInterceptor<?>> disconnectInterceptors;

    @Bean
    @ConditionalOnMissingBean(ConnectController.class)
    public ConnectController connectController(
            ConnectionFactoryLocator factoryLocator,
            ConnectionRepository repository) {
        ConnectController controller = new ConnectController(factoryLocator,
                repository);
        if (!CollectionUtils.isEmpty(this.connectInterceptors)) {
            controller.setConnectInterceptors(this.connectInterceptors);
        }
        if (!CollectionUtils.isEmpty(this.disconnectInterceptors)) {
            controller.setDisconnectInterceptors(this.disconnectInterceptors);
        }
        return controller;
    }

    /* needed when using a step between social sign in and coduno signup
     * eg choosing a username, email, etc

    @Autowired(required = false)
    private List<ProviderSignInInterceptor<?>> signInInterceptors;

    @Bean
    @ConditionalOnBean(SignInAdapter.class)
    @ConditionalOnMissingBean
    public ProviderSignInController signInController(
            ConnectionFactoryLocator factoryLocator,
            UsersConnectionRepository usersRepository, SignInAdapter signInAdapter) {
        ProviderSignInController controller = new ProviderSignInController(
                factoryLocator, usersRepository, signInAdapter);
        if (!CollectionUtils.isEmpty(this.signInInterceptors)) {
            controller.setSignInInterceptors(this.signInInterceptors);
        }
        return controller;
    }
    */
}
