package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.Map;

/**
 * Created by vbalan on 2/25/2016.
 */

@Entity
@Table(name = "test")
public class Test extends IdentifiableEntity {
    public static final String PATH = "path";
    public static final String STDIN = "stdin";

    @ManyToOne
    private Runner runner;

    @ManyToOne
    private Task task;

    @ElementCollection(fetch = FetchType.EAGER)
    @Lob
    private Map<String, String> params;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Runner getRunner() {
        return runner;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
