package uno.cod.platform.server.core.dto.runner;

import uno.cod.platform.server.core.domain.Runner;

import java.util.UUID;

public class RunnerShowDto {
    private UUID id;
    private String name;
    private String canonicalName;

    public RunnerShowDto(Runner runner){
        this.id = runner.getId();
        this.canonicalName = runner.getCanonicalName();
        this.name = runner.getName();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
