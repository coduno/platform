package uno.cod.platform.server.core.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class TaskResultKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = false)
    private Result result;

    @ManyToOne(optional = false)
    private Task task;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public int hashCode() {
        int hash = task.hashCode();
        hash = 31 * hash + result.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskResultKey that = (TaskResultKey) o;

        if (task != null ? !task.equals(that.task) : that.task != null) {
            return false;
        }
        return !(result != null ? !result.equals(that.result) : that.result != null);

    }
}
