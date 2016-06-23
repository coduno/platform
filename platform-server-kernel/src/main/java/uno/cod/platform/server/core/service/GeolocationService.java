package uno.cod.platform.server.core.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

/**
 * A wrapper around the <a href="http://dev.maxmind.com/geoip/geoip2/geolite2/">MaxMind GeoLite2 Free Downloadable Database</a>.
 *
 * Upon initialization, the newest version of the database is fetched (around ~1.1MiB). Subsequent calls to
 * {@link #lookupCountry(InetAddress)} will be answered from memory.
 *
 * If for some reason the database cannot be fetched and/or initialized, all lookups will return the empty string.
 */
@Service
public class GeolocationService {
    private static final Logger LOG = LoggerFactory.getLogger(GeolocationService.class);

    @Value("${coduno.geolocation.url}")
    private String url;

    private DatabaseReader reader;

    @PostConstruct
    private void loadDatabase() {
        try {
            reader = new DatabaseReader.Builder(new URL(url).openStream()).build();
        } catch (IOException e) {
            LOG.debug("Failed to build Geolocation Database!", e);
        }
    }

    @PreDestroy
    private void unloadDatabase() {
        if (reader == null) {
            return;
        }
        try {
            reader.close();
        } catch (IOException ioe) {
            LOG.debug("Failed to close database. Forcefully closing it anyway.", ioe);
        }
        reader = null;
    }

    /**
     * Deletes the in-memory database and attempts to fetch new data.
     */
    public void reloadDatabase() {
        unloadDatabase();
        loadDatabase();
    }

    /**
     * Looks up a country based on internet address.
     * @param address the internet address to look up.
     * @return an ISO 3166-1 alpha-2 compliant country code or the empty string if lookup failed.
     * @see <a href="http://www.iso.org/iso/country_codes">ISO Country Codes</a>
     */
    public String lookupCountry(InetAddress address) {
        if (reader == null) {
            return "";
        }
        try {
            return reader.country(address).getCountry().getIsoCode();
        } catch (GeoIp2Exception | IOException e) {
            LOG.debug("Failed to look up {}!", address.toString(), e);
            return "";
        }
    }
}
