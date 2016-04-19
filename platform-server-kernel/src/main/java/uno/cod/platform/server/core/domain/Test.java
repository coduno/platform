package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "test")
public class Test extends IdentifiableEntity {
    @ManyToOne
    private Runner runner;

    @ManyToOne
    private Task task;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "test_params",
            joinColumns = {@JoinColumn(name = "test_id")})
    @MapKeyColumn(name = "params_key")
    @Column(name = "params")
    @Lob
    private Map<String, String> params;

    @Column(name = "custom_index")
    private int index;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
