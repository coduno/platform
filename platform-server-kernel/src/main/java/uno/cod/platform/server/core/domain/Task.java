package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.*;

/**
 * A task is an atomic challenge that runs on the
 * coduno runtime
 */
@Entity
@Table(name = "task")
public class Task extends Assignment {
    @ManyToMany(mappedBy = "tasks")
    private List<ChallengeTemplate> challengeTemplates;

    @ManyToOne
    private Endpoint endpoint;

    @ManyToOne
    private Organization organization;

    @Column
    private boolean isPublic;

    @ElementCollection
    @CollectionTable(name = "task_skillmap")
    private Map<CodingSkill, Double> skillMap;

    @OneToMany(mappedBy = "task")
    private List<Test> tests;

    @OneToMany(mappedBy = "task")
    private List<Template> templates;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "runner_id")
    private Runner runner;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_params")
    @Lob
    private Map<String, String> params;

    @ManyToMany
    private Set<Language> languages;

    public List<ChallengeTemplate> getChallengeTemplates() {
        return Collections.unmodifiableList(challengeTemplates);
    }

    protected void setChallengeTemplates(List<ChallengeTemplate> challengeTemplates) {
        this.challengeTemplates = challengeTemplates;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<Test> getTests() {
        return Collections.unmodifiableList(tests);
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public Map<CodingSkill, Double> getSkillMap() {
        return skillMap;
    }

    public void setSkillMap(Map<CodingSkill, Double> skillMap) {
        this.skillMap = skillMap;
    }

    public Runner getRunner() {
        return runner;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public Set<Language> getLanguages() {
        return Collections.unmodifiableSet(languages);
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    protected void addChallenge(ChallengeTemplate challengeTemplate) {
        if (challengeTemplates == null) {
            challengeTemplates = new ArrayList<>();
        }
        challengeTemplates.add(challengeTemplate);
    }

    public void addTest(Test test) {
        if (tests == null) {
            tests = new ArrayList<>();
        }
        tests.add(test);
        test.setTask(this);
    }

    public void addSkill(CodingSkill skill, Double value) {
        if (skillMap == null) {
            skillMap = new HashMap<>();
        }
        skillMap.put(skill, value);
    }

    public List<Template> getTemplates() {
        return Collections.unmodifiableList(templates);
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    public void addTemplate(Template template){
        if (templates == null){
            templates = new ArrayList<>();
        }
        templates.add(template);
    }

    public void addLanguage(Language language){
        if(languages == null){
            languages = new HashSet<>();
        }
        languages.add(language);
    }
}

