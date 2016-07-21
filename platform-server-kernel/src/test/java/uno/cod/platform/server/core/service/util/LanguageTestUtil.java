package uno.cod.platform.server.core.service.util;

import uno.cod.platform.server.core.domain.Language;

import java.util.UUID;

public class LanguageTestUtil {
    public static Language getLanguage() {
        return new Language(UUID.randomUUID(), "java", "Java");
    }
}
