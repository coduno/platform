package uno.cod.platform.server.core.config.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.provider.OAuth1AuthenticationService;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.Assert;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.social.AccountConnectionSignUp;
import uno.cod.platform.server.core.social.JdbcUsersConnectionRepository;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class SocialConfiguration {
    @Autowired
    private Environment environment;

    @Autowired
    DataSource dataSource;

    private List<SocialConfigurer> socialConfigurers;

    @Autowired
    public void setSocialConfigurers(List<SocialConfigurer> socialConfigurers) {
        Assert.notNull(socialConfigurers, "At least one configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
        Assert.notEmpty(socialConfigurers, "At least one configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
        this.socialConfigurers = socialConfigurers;
    }

    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        MySecurityEnabledConnectionFactoryConfigurer cfConfig = new MySecurityEnabledConnectionFactoryConfigurer();
        for (SocialConfigurer socialConfigurer : socialConfigurers) {
            socialConfigurer.addConnectionFactories(cfConfig, environment);
        }
        return cfConfig.getConnectionFactoryLocator();
    }

    @Bean
    public UserIdSource userIdSource() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof SocialUserDetails)) {
                throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
            }
            return ((SocialUserDetails) authentication.getPrincipal()).getUserId();
        };
    }

    @Bean
    public UsersConnectionRepository usersConnectionRepository(UserRepository userRepository) {
        JdbcUsersConnectionRepository jdbcUsersConnectionRepository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator(), Encryptors.noOpText());
        jdbcUsersConnectionRepository.setConnectionSignUp(new AccountConnectionSignUp(userRepository));
        return jdbcUsersConnectionRepository;
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionRepository connectionRepository(UsersConnectionRepository usersConnectionRepository) {
        return usersConnectionRepository.createConnectionRepository(userIdSource().getUserId());
    }

}

class MySecurityEnabledConnectionFactoryConfigurer implements ConnectionFactoryConfigurer {

    private SocialAuthenticationServiceRegistry registry;

    public MySecurityEnabledConnectionFactoryConfigurer() {
        registry = new SocialAuthenticationServiceRegistry();
    }

    public void addConnectionFactory(ConnectionFactory<?> connectionFactory) {
        registry.addAuthenticationService(wrapAsSocialAuthenticationService(connectionFactory));
    }

    public ConnectionFactoryRegistry getConnectionFactoryLocator() {
        return registry;
    }


    private <A> SocialAuthenticationService<A> wrapAsSocialAuthenticationService(ConnectionFactory<A> cf) {
        if (cf instanceof OAuth1ConnectionFactory) {
            return new OAuth1AuthenticationService<A>((OAuth1ConnectionFactory<A>) cf);
        } else if (cf instanceof OAuth2ConnectionFactory) {
            final OAuth2AuthenticationService<A> authService = new OAuth2AuthenticationService<A>((OAuth2ConnectionFactory<A>) cf);
            authService.setDefaultScope(((OAuth2ConnectionFactory<A>) cf).getScope());
            return authService;
        }
        throw new IllegalArgumentException("The connection factory must be one of OAuth1ConnectionFactory or OAuth2ConnectionFactory");
    }

}
