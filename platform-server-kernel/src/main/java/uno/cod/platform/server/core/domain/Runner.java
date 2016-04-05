package uno.cod.platform.server.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "runner")
public class Runner extends IdentifiableEntity {
    @Column
    private String name;

    @Column(name = "canonical_name", unique = true, nullable = false)
    private String canonicalName;

    @OneToMany(mappedBy = "runner")
    private List<Test> tests;

    @OneToMany(mappedBy = "runner")
    private List<Task> tasks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Test> getTests() {
        return Collections.unmodifiableList(tests);
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
