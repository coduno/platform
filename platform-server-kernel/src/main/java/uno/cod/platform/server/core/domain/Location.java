package uno.cod.platform.server.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Location extends IdentifiableEntity {
    @Column(unique = true, nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
