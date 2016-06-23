package uno.cod.platform.server.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.service.GeolocationService;
import uno.cod.platform.server.core.service.TermsService;
import uno.cod.platform.server.core.service.UserService;
import uno.cod.platform.server.rest.RestUrls;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Locale;

@RestController
public class TermsController {
    private final TermsService termsService;
    private final GeolocationService geolocationService;
    private final UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public TermsController(TermsService termsService, GeolocationService geolocationService, UserService userService) {
        this.termsService = termsService;
        this.geolocationService = geolocationService;
        this.userService = userService;
    }

    @RequestMapping(value = RestUrls.TERMS, method = RequestMethod.GET)
    public ResponseEntity get() {
        Resource terms = null;
        Enumeration<Locale> l = request.getLocales();

        // If the client sends a a country
        while (l.hasMoreElements() && terms == null) {
            String country = l.nextElement().getCountry();

            // Lazy check for an ISO 3166-1 alpha-2 conform string.
            if (!country.isEmpty() && country.length() == 2) {
                terms = termsService.getTerms(country);
            }
        }

        if (terms == null) {
            // If the XFF header was set by a proxy, use
            // that so we get the IP of the real client.
            String ip = request.getHeader("X-Forwarded-For");

            if (ip == null) {
                request.getRemoteAddr();
            } else {
                // XFF may be a list of proxies. Filter out the client address.
                ip = ip.split(", ")[0];
            }

            try {
                terms = termsService.getTerms(geolocationService.lookupCountry(InetAddress.getByName(ip)));
            } catch (UnknownHostException e) {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"));
        return new ResponseEntity<>(terms, headers, HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.TERMS_ACCEPT, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity accept(@AuthenticationPrincipal User user) {
        userService.acceptTerms(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
