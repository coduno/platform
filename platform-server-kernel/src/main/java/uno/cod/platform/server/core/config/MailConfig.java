package uno.cod.platform.server.core.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import uno.cod.platform.thymeleaf.CodunoDialect;

import static uno.cod.platform.server.core.Profiles.DEVELOPMENT;

@Configuration
@EnableAsync
public class MailConfig {
    @Autowired
    Environment env;

    @Value("${coduno.url}")
    String appUrl;

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(emailTemplateResolver());
        templateEngine.addDialect(new CodunoDialect(appUrl));
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    /**
     * THYMELEAF: Template Resolver for email templates.
     */
    private TemplateResolver emailTemplateResolver() {
        TemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setOrder(1);
        if (env.acceptsProfiles(DEVELOPMENT)) {
            templateResolver.setCacheable(false);
        }
        return templateResolver;
    }
}