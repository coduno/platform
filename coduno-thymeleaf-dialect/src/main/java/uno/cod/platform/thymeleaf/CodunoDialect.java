package uno.cod.platform.thymeleaf;


import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class CodunoDialect extends AbstractDialect {
    private String appUrl;

    public CodunoDialect(String appUrl) {
        super();
        this.appUrl = appUrl;
    }

    public String getPrefix() {
        return "cod";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new CodunoHrefProcessor(appUrl));
        return processors;
    }
}
