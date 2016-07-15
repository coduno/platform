package uno.cod.platform.server.core.filter;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.service.ActivationTokenService;
import uno.cod.platform.server.core.util.TokenHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


/**
 * This filter authenticates via a non standardized Authorization Header. We
 * are using this approach since a full OAuth2 implementation at the current
 * time would just increase our complexity, for little or none benefits.
 *
 * Header Scheme: "Authorization: Token base64([ID]:[token])"
 *
 * Registered Schemes can be found here: http://www.iana.org/assignments/http-authschemes/http-authschemes.xhtml
 */
public class ActivationTokenAuthenticationFilter extends OncePerRequestFilter {
    private ActivationTokenService activationTokenService;

    public ActivationTokenAuthenticationFilter(ActivationTokenService activationTokenService) {
        this.activationTokenService = activationTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        final boolean debug = logger.isDebugEnabled();

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Token ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String[] authHeader = TokenHelper.decodeAndSplit(header.substring(6));

            if (authHeader.length != 2) {
                throw new BadCredentialsException("Failed to decode basic authentication token");
            }

            if (debug) {
                logger.debug("Decoded header with ID " + authHeader[0] + " and secret " + authHeader[1]);
            }

            UUID id = UUID.fromString(authHeader[0]);
            String token = authHeader[1];
            UserDetails user = activationTokenService.loadByActivationToken(id, token);

            if (user == null) {
                throw new CodunoNoSuchElementException("token.invalid");
            }

            if (debug) {
                logger.debug("Token Authentication Authorization header found for user '"
                        + user.getUsername() + "'. Authenticating.");
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();

            if (debug) {
                logger.debug("Authentication request for failed: " + failed);
            }

            return;
        }
        chain.doFilter(request, response);
    }
}
