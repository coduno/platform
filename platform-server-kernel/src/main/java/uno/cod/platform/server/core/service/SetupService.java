package uno.cod.platform.server.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.Profiles;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.repository.*;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.*;

@Service
@Transactional
public class SetupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetupService.class);
    private final Environment environment;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EndpointRepository endpointRepository;
    private final TaskRepository taskRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipRepository organizationMembershipRepository;
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final RunnerRepository runnerRepository;
    private final TestRepository testRepository;
    private final LanguageRepository languageRepository;

    @Autowired
    public SetupService(Environment environment, PasswordEncoder passwordEncoder, UserRepository userRepository, EndpointRepository endpointRepository, TaskRepository taskRepository, OrganizationRepository organizationRepository, OrganizationMembershipRepository organizationMembershipRepository, ChallengeTemplateRepository challengeTemplateRepository, RunnerRepository runnerRepository, TestRepository testRepository, LanguageRepository languageRepository) {
        this.environment = environment;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.endpointRepository = endpointRepository;
        this.taskRepository = taskRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
        this.challengeTemplateRepository = challengeTemplateRepository;
        this.runnerRepository = runnerRepository;
        this.testRepository = testRepository;
        this.languageRepository = languageRepository;
    }

    public void init() {
        if (Arrays.asList(this.environment.getActiveProfiles()).contains(Profiles.DEVELOPMENT)) {
            LOGGER.info("initializing development database");
            this.initDevelopmentDatabase();
        }
    }

    private void initDevelopmentDatabase() {
        Organization catalysts = initCatalystsWithUsers();
        Set<Language> cccLanguages = initCCCLanguages();

        Endpoint cccTaskEndpoint = createEndpoint("CCC task", "ccc-drone-task");
        Endpoint cccChallengeEndpoint = createEndpoint("CCC challenge", "ccc-challenge");
        createEndpoint("CCC general task", "ccc-io-task");

        String[] ccc = new String[]{".", ".", ".", ".", ".", ".", "."};
        String[] cccTaskNames = new String[]{"drones-level-1", "drones-level-2",
                "drones-level-3", "drones-level-4", "drones-level-5", "drones-level-6", "drones-level-7"};

        String[] schoolCCC = new String[]{".", ".", ".", ".", "."};
        String[] schoolCCCTaskNames = new String[]{"drones-2d-level-1", "drones-2d-level-2",
                "drones-2d-level-3", "drones-2d-level-4", "drones-2d-level-5"};

        createRunner("/diff");
        createRunner("/io");

        Runner testRunner = createRunner("/drones/test");
        Runner normalRunner = createRunner("/drones/run");
        initCCC(catalysts, cccLanguages, cccChallengeEndpoint, cccTaskEndpoint, ccc, cccTaskNames,
                Duration.ofHours(4), "drones", testRunner, normalRunner);
        Runner schoolTestRunner = createRunner("/drones-2d/test");
        Runner schoolNormalRunner = createRunner("/drones-2d/run");
        initCCC(catalysts, cccLanguages, cccChallengeEndpoint, cccTaskEndpoint, schoolCCC, schoolCCCTaskNames,
                Duration.ofHours(2), "drones-2d", schoolTestRunner, schoolNormalRunner);
    }

    private Organization initCatalystsWithUsers() {
        Organization catalysts = new Organization("catalysts", "Catalysts GmbH");
        catalysts = organizationRepository.save(catalysts);

        String password = this.passwordEncoder.encode("Whatismypassword?");

        List<User> users = new ArrayList<>();
        users.add(createUser("flowlo", "lorenz.leutgeb@cod.uno", password, true));
        users.add(createUser("jenglisch", "jakob.englisch@cod.uno", password, true));
        users.add(createUser("steindl", "christoph.steindl@catalysts.cc", password, true));

        for (User user : users) {
            createMembership(user, catalysts);
        }
        return catalysts;
    }

    private Set<Language> initCCCLanguages() {
        Set<Language> languages = new HashSet<>();
        languages.add(createLanguage("Java", "java"));
        languages.add(createLanguage("Python", "py"));
        languages.add(createLanguage("C", "c"));
        languages.add(createLanguage("C++", "cpp"));
        languages.add(createLanguage("C#", "csharp"));
        languages.add(createLanguage("Go", "go"));
        languages.add(createLanguage("Scala", "scala"));
        languages.add(createLanguage("Php", "php"));
        languages.add(createLanguage("Groovy", "groovy"));
        languages.add(createLanguage("Javascript", "js"));
        return languages;
    }

    private void initCCC(Organization catalysts, Set<Language> languages,
                         Endpoint cccChallengeEndpoint, Endpoint cccEndpoint,
                         String[] instructions, String[] taskNames, Duration duration, String name,
                         Runner testRunner, Runner normalRunner) {

        ChallengeTemplate ccc = new ChallengeTemplate(name, name);
        ccc.setDescription("## Description");
        ccc.setInstructions("## Instructions");
        ccc.setOrganization(catalysts);
        ccc.setEndpoint(cccChallengeEndpoint);
        ccc.setDuration(duration);

        for (int i = 0; i < instructions.length; i++) {
            Map<String, String> taskParams = new HashMap<>();
            taskParams.put("level", (i + 1) + "");
            taskParams.put("test", "1");
            Task task = createTask("Level " + (i + 1), taskNames[i], "## Description", instructions[i],
                    cccEndpoint, normalRunner, Duration.ofHours(4), catalysts, languages, taskParams);
            for (int j = 1; j <= 3; j++) {
                Map<String, String> params = new HashMap<>();
                params.put("level", (i + 1) + "");
                params.put("test", j + "");
                createTest(task, testRunner, j - 1, params);
            }
            ccc.addTask(task);
        }
        challengeTemplateRepository.save(ccc);
    }

    private Runner createRunner(String path) {
        Runner runner = new Runner();
        runner.setPath(path);
        return runnerRepository.save(runner);
    }

    private Endpoint createEndpoint(String name, String component) {
        Endpoint outputMatchEndpoint = new Endpoint();
        outputMatchEndpoint.setName(name);
        outputMatchEndpoint.setComponent(component);
        return endpointRepository.save(outputMatchEndpoint);
    }

    private Task createTask(String name, String canonicalName, String description, String instructions, Endpoint endpoint, Runner runner, Duration duration, Organization organization, Set<Language> languages, Map<String, String> params) {
        Task task = new Task(canonicalName, name);
        task.setDescription(description);
        task.setInstructions(instructions);
        task.setEndpoint(endpoint);
        task.setDuration(duration);
        task.setRunner(runner);
        task.addSkill(CodingSkill.CODING_SPEED, 1D);
        task.setOrganization(organization);
        task.setLanguages(languages);
        task.setParams(params);
        if (organization == null) {
            task.setPublic(true);
        }
        return taskRepository.save(task);
    }

    private Test createTest(Task task, Runner runner, int index, Map<String, String> params) {
        Test helloWorldTest = new Test();
        helloWorldTest.setTask(task);
        helloWorldTest.setRunner(runner);
        helloWorldTest.setParams(params);
        helloWorldTest.setIndex(index);
        return testRepository.save(helloWorldTest);
    }

    private User createUser(String username, String email, String password, Boolean admin) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setAdmin(admin);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private void createMembership(User user, Organization organization) {
        OrganizationMembershipKey key = new OrganizationMembershipKey();
        key.setUser(user);
        key.setOrganization(organization);

        OrganizationMembership membership = new OrganizationMembership();
        membership.setKey(key);
        membership.setAdmin(true);
        organizationMembershipRepository.save(membership);
    }

    private Language createLanguage(String name, String canonicalName) {
        Language language = new Language(canonicalName, name);
        return languageRepository.save(language);
    }
}
