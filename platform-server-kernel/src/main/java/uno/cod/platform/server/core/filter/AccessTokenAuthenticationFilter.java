package uno.cod.platform.server.core.filter;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.filter.OncePerRequestFilter;
import uno.cod.platform.server.core.service.AccessTokenService;

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
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private AccessTokenService accessTokenService;

    public AccessTokenAuthenticationFilter(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final boolean debug = logger.isDebugEnabled();

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Token ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String[] authHeader = extractAndDecodeHeader(header);

            if (authHeader.length != 2) {
                throw new BadCredentialsException("Failed to decode basic authentication token");
            }

            UserDetails user = accessTokenService.loadByAccessToken(UUID.fromString(authHeader[0]), authHeader[1]);

            if (user == null) {
                throw new BadCredentialsException("No user with this token found");
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

    /**
     * Decodes the header into a username and password.
     *
     * @throws BadCredentialsException if the Basic header is not present or is not valid
     * Base64
     */
    private String[] extractAndDecodeHeader(String header)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, "UTF-8");

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[] {
                token.substring(0, delim), token.substring(delim + 1)
        };
    }
}
