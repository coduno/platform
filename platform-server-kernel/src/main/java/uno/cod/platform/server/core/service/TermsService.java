package uno.cod.platform.server.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TermsService {
    private static final Logger LOG = LoggerFactory.getLogger(TermsService.class);

    @Autowired
    private ApplicationContext appContext;

    /**
     * Returns the current terms for a given country.
     * @param country ISO 3166-1 alpha-2 conform country code of the coutnry to get the terms for.
     * @return a stream of the file that contains the terms or <code>null</code> if no terms were found.
     */
    public Resource getTerms(String country) {
        String pattern = "terms/" + country + "/????-??-??.md";
        Resource[] resources;
        try {
            resources = appContext.getResources(pattern);
        } catch (IOException e) {
            LOG.warn("Failed to get terms for pattern {}", pattern, e);
            return null;
        }

        Resource resource = null;
        Duration min = ChronoUnit.FOREVER.getDuration();
        ZonedDateTime now = ZonedDateTime.now();
        for (Resource r : resources) {
            String name = Paths.get(r.getFilename()).getFileName().toString();
            Duration duration = Duration.between(ZonedDateTime.parse(name.substring(0, name.length() - 3)), now);

            if (duration.isNegative()) {
                continue;
            }

            if (duration.compareTo(min) <= 0) {
                resource = r;
                min = duration;
            }
        }

        return resource;
    }
}
