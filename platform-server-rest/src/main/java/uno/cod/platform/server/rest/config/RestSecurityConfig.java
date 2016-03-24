package uno.cod.platform.server.rest.config;

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
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint())
            .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
            .and()
                .logout()
            .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/setup",
                        "/organizations",
                        "/users",
                        "/ip",
                        "/invite/auth/*").permitAll()
                .anyRequest().authenticated()
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
