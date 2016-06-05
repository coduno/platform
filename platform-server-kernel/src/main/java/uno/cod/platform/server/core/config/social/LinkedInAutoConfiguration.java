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
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;

@Configuration
@ConditionalOnClass({ SocialConfigurerAdapter.class, LinkedInConnectionFactory.class })
@ConditionalOnProperty(prefix = "spring.social.linkedin", name = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class LinkedInAutoConfiguration {

    @Configuration
    @EnableConfigurationProperties(LinkedInProperties.class)
    @ConditionalOnWebApplication
    protected static class LinkedInConfigurerAdapter extends SocialConfigurerAdapter {

        @Autowired
        private LinkedInProperties properties;

        @Bean
        @ConditionalOnMissingBean(LinkedIn.class)
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public LinkedIn linkedin(ConnectionRepository repository) {
            Connection<LinkedIn> connection = repository
                    .findPrimaryConnection(LinkedIn.class);
            return connection != null ? connection.getApi() : null;
        }

        @Override
        public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
            connectionFactoryConfigurer.addConnectionFactory(new LinkedInConnectionFactory(this.properties.getAppId(),
                    this.properties.getAppSecret()));
        }
    }

}
