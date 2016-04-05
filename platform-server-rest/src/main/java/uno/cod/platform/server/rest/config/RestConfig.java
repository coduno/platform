package uno.cod.platform.server.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uno.cod.platform.server.core.Profiles;

import javax.servlet.MultipartConfigElement;

@Configuration
@ComponentScan({"uno.cod.platform.server.rest"})
public class RestConfig {
    @Value("${coduno.url}")
    private String codunoUrl;

    @Value("${coduno.codingcontest_url}")
    private String codingContestUrl;

    @Bean(name = "exceptionMessageSource")
    public ResourceBundleMessageSource exceptionMessageSource() {
        ResourceBundleMessageSource bean = new ResourceBundleMessageSource();
        bean.setFallbackToSystemLocale(false);
        bean.setBasename("i18n/exceptions");
        return bean;
    }

    @Bean
    @Profile(Profiles.DEVELOPMENT)
    public WebMvcConfigurer devCorsConfigurer(){
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

    @Bean
    @Profile({Profiles.PRODUCTION, Profiles.APPENGINE})
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/contestuploadraw").allowedOrigins(codingContestUrl);
                registry.addMapping("/uploaduserraw").allowedOrigins(codingContestUrl);
                registry.addMapping("/api/contests/*/report/json").allowedOrigins(codingContestUrl);
                registry.addMapping("/**").allowedOrigins(codunoUrl);
            }
        };
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("16mb");
        factory.setMaxRequestSize("16mb");
        return factory.createMultipartConfig();
    }
}
