package uno.cod.platform.server.core.dto.organization;

import uno.cod.platform.server.core.domain.Organization;

import java.util.UUID;

public class OrganizationShowDto {
    private UUID id;
    private String name;
    private String nick;

    public OrganizationShowDto(Organization organization) {
        this.id = organization.getId();
        this.name = organization.getName();
        this.nick = organization.getCanonicalName();
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

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
