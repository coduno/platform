package uno.cod.platform.server.core.dto.team;

import uno.cod.platform.server.core.util.constraints.CanonicalName;

import javax.validation.constraints.NotNull;

public class TeamCreateDto {
    @NotNull
    private String name;

    @CanonicalName
    private String canonicalName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
