package uno.cod.platform.server.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.social.security.SpringSocialConfigurer;
import uno.cod.platform.server.core.filter.AccessTokenAuthenticationFilter;
import uno.cod.platform.server.core.filter.ActivationTokenAuthenticationFilter;
import uno.cod.platform.server.core.service.AccessTokenService;
import uno.cod.platform.server.core.service.ActivationTokenService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private ActivationTokenService activationTokenService;

    /*@Autowired
    public RestSecurityConfig(AccessTokenService accessTokenService, ActivationTokenService activationTokenService) {
        this.accessTokenService = accessTokenService;
        this.activationTokenService = activationTokenService;
    }*/

    @Value("${coduno.url}")
    private String appUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .logout()
                    .clearAuthentication(false) // spring session bug - https://jira.spring.io/browse/SEC-3070
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl(appUrl)
                .and()
                .addFilterBefore(new ActivationTokenAuthenticationFilter(activationTokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new AccessTokenAuthenticationFilter(accessTokenService), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/setup",
                        "/organizations",
                        "/challenges/*",
                        "/users",
                        "/ip",
                        "/user/password/reset",
                        "/auth/**",
                        "/activation_tokens",
                        "/invite/auth/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new SpringSocialConfigurer()
                        .postLoginUrl(appUrl)
                        .signupUrl(appUrl)
                        .defaultFailureUrl(appUrl))
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        http.csrf().disable();
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        return new CookieHttpSessionStrategy();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new XhrBasicAuthenticationEntryPoint();
    }

    private class XhrBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
        XhrBasicAuthenticationEntryPoint() {
            this.setRealmName("dummy");
        }

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException, ServletException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    authException.getMessage());
        }
    }
}
