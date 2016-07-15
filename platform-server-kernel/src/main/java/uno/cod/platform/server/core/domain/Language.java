package uno.cod.platform.server.core.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "language")
public class Language extends NamedEntity {
    @ManyToMany(mappedBy = "languages")
    private Set<Task> tasks;

    public Language(UUID id, String canonicalName, String name) {
        super(id, canonicalName, name);
    }

    public Language(String canonicalName, String name) {
        super(canonicalName, name);
    }

    protected Language() {
        super();
    }

    public Set<Task> getTasks() {
        return Collections.unmodifiableSet(tasks);
    }
    protected void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
