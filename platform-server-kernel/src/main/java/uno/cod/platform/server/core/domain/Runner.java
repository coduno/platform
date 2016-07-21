package uno.cod.platform.server.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "runner")
public class Runner extends IdentifiableEntity {
    @Column(unique = true, nullable = false)
    private String path;

    @OneToMany(mappedBy = "runner")
    private List<Test> tests;

    @OneToMany(mappedBy = "runner")
    private List<Task> tasks;

    public Runner(UUID id) {
        super(id);
    }

    public Runner() {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
