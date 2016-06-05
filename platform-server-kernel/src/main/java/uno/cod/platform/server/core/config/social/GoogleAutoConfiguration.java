package uno.cod.platform.server.core.config.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;

@Configuration
@ConditionalOnClass({ SocialConfigurerAdapter.class, GoogleConnectionFactory.class })
@ConditionalOnProperty(prefix = "spring.social.google", name = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class GoogleAutoConfiguration {

    @Configuration
    @EnableConfigurationProperties(GoogleProperties.class)
    @ConditionalOnWebApplication
    protected static class GoogleConfigurerAdapter extends SocialConfigurerAdapter {

        @Autowired
        private GoogleProperties properties;

        @Bean
        @ConditionalOnMissingBean(Google.class)
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public Google google(ConnectionRepository repository) {
            Connection<Google> connection = repository
                    .findPrimaryConnection(Google.class);
            return connection != null ? connection.getApi() : null;
        }

        @Override
        public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
            connectionFactoryConfigurer.addConnectionFactory(new GoogleConnectionFactory(this.properties.getAppId(),
                    this.properties.getAppSecret()));
        }
    }

}