package uno.cod.platform.server.core.dto.language;

import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.Language;

import java.util.UUID;

public class LanguageShowDto {
    private UUID id;
    private String canonicalName;
    private String name;

    public LanguageShowDto(Language language) {
        BeanUtils.copyProperties(language, this);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
