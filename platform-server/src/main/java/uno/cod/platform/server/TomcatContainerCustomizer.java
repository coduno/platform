package uno.cod.platform.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static uno.cod.platform.server.core.Profiles.APPENGINE;

// cuz of https://code.google.com/p/googleappengine/issues/detail?id=12252
// hate already started: https://github.com/GoogleCloudPlatform/php-docker/pull/38
@Component
@Profile(APPENGINE)
public class TomcatContainerCustomizer implements EmbeddedServletContainerCustomizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatContainerCustomizer.class);

    @Override
    public void customize(final ConfigurableEmbeddedServletContainer container) {
        if (container instanceof TomcatEmbeddedServletContainerFactory) {
            final TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
            tomcat.addConnectorCustomizers(connector -> {
                connector.setScheme("https");
                connector.setProxyPort(443);
            });
            LOGGER.info("Enabled secure scheme (https).");
        } else {
            LOGGER.warn("Could not change protocol scheme because Tomcat is not used as servlet container.");
        }
    }
}