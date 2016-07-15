package uno.cod.platform.server.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "endpoint")
public class Endpoint extends IdentifiableEntity {
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String component;

    @OneToMany(mappedBy = "endpoint")
    private List<Task> tasks;

    @OneToMany(mappedBy = "endpoint")
    private List<ChallengeTemplate> challengeTemplates;

    public Endpoint(UUID id) {
        super(id);
    }

    public Endpoint() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    protected void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<ChallengeTemplate> getChallengeTemplates() {
        return Collections.unmodifiableList(challengeTemplates);
    }

    protected void setChallengeTemplates(List<ChallengeTemplate> challengeTemplates) {
        this.challengeTemplates = challengeTemplates;
    }

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        task.setEndpoint(this);
        tasks.add(task);
    }

    public void addChallenge(ChallengeTemplate challengeTemplate) {
        if (challengeTemplate == null) {
            challengeTemplates = new ArrayList<>();
        }
        challengeTemplate.setEndpoint(this);
        challengeTemplates.add(challengeTemplate);
    }
}
