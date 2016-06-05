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
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.google.api.Google;

@Configuration
@ConditionalOnClass({ SocialConfigurerAdapter.class, GitHubConnectionFactory.class })
@ConditionalOnProperty(prefix = "spring.social.github", name = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class GitHubAutoConfiguration {

    @Configuration
    @EnableConfigurationProperties(GitHubProperties.class)
    @ConditionalOnWebApplication
    protected static class GitHubConfigurerAdapter extends SocialConfigurerAdapter {

        @Autowired
        private GitHubProperties properties;

        @Bean
        @ConditionalOnMissingBean(Google.class)
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public GitHub github(ConnectionRepository repository) {
            Connection<GitHub> connection = repository
                    .findPrimaryConnection(GitHub.class);
            return connection != null ? connection.getApi() : null;
        }

        @Override
        public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
            connectionFactoryConfigurer.addConnectionFactory(new GitHubConnectionFactory(this.properties.getAppId(),
                    this.properties.getAppSecret()));
        }
    }

}