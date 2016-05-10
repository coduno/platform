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

    @Column(name = "canonical_name", nullable = false, unique = true)
    private String canonicalName;

    @ManyToOne
    private Endpoint endpoint;

    @ManyToOne
    private Organization organization;

    @Column
    private boolean isPublic;

    @ElementCollection
    @MapKeyColumn(name = "skill_map_key")
    @Column(name = "skill_map")
    @CollectionTable(name = "task_skill_map",
            joinColumns = {@JoinColumn(name = "task_id")})
    private Map<CodingSkill, Double> skillMap;

    @OneToMany(mappedBy = "task")
    private List<Test> tests;

    /**
     * Maps from names in backing storage
     * to human readable ones.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "template_value")
    @MapKeyColumn(name = "template_key")
    @CollectionTable(name = "task_templates",
            joinColumns = {@JoinColumn(name = "task_id")})
    @Lob
    private Map<String, String> templates;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "runner_id")
    private Runner runner;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "params")
    @MapKeyColumn(name = "params_key")
    @CollectionTable(name = "task_params",
            joinColumns = {@JoinColumn(name = "task_id")})
    @Lob
    private Map<String, String> params;

    @ManyToMany
    private Set<Language> languages;

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

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

    public Map<String, String> getTemplates() {
        if (templates == null) {
            return null;
        }
        return Collections.unmodifiableMap(templates);
    }

    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }

    public void putTemplate(String objectName, String readableName) {
        if (templates == null) {
            templates = new HashMap<>();
        }
        templates.put(objectName, readableName);
    }

    public void addLanguage(Language language) {
        if (languages == null) {
            languages = new HashSet<>();
        }
        languages.add(language);
    }
}

