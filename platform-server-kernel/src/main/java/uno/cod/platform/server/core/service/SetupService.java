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
    private static final String TAG = SetupService.class.getSimpleName();
    private final Environment environment;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EndpointRepository endpointRepository;
    private final TaskRepository taskRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final RunnerRepository runnerRepository;
    private final TestRepository testRepository;
    private final TemplateRepository templateRepository;
    private final LanguageRepository languageRepository;

    @Autowired
    public SetupService(Environment environment, PasswordEncoder passwordEncoder, UserRepository userRepository, EndpointRepository endpointRepository, TaskRepository taskRepository, OrganizationRepository organizationRepository, OrganizationMemberRepository organizationMemberRepository, ChallengeTemplateRepository challengeTemplateRepository, RunnerRepository runnerRepository, TestRepository testRepository, TemplateRepository templateRepository, LanguageRepository languageRepository) {
        this.environment = environment;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.endpointRepository = endpointRepository;
        this.taskRepository = taskRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.challengeTemplateRepository = challengeTemplateRepository;
        this.runnerRepository = runnerRepository;
        this.testRepository = testRepository;
        this.templateRepository = templateRepository;
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

        String[] ccc = new String[]{
                "https://drive.google.com/open?id=122J6m6N8Xoj0vuLd7aqvslDh7fLNfl_lZQXINVnBOL4",
                "https://drive.google.com/open?id=1ifaPjiuSFIFS8qbaVS7QeHVN1BzGtOSEnuPnhAaqkDk",
                "https://drive.google.com/open?id=1GgrxxDMtH_QGk8izNEDu4GbKkLTx9CrIk0a6-B4wHww",
                "https://drive.google.com/open?id=1BYTOgdr6DBtA1N6zRRFLF9ZQOq0jzS49yvwR6O8KQ0M",
                "https://drive.google.com/open?id=1xWQlDKtcF4F5-NzPw-WN9XqfbsLs1dZE9mdsNjtS-gM",
                "https://drive.google.com/open?id=13DwC_eFwpqok5AXpItPv3H2eOCV8z-Y_kI1n4_2sB8c",
                "https://drive.google.com/open?id=1VT_JLK8ulQHuEyMdLB8FJi8okpN0Q7GkUpl_f9lWkZY"
        };

        String[] schoolCCC = new String[]{
                "https://drive.google.com/open?id=15chrrkSvZzxtzK6orWWuiSXzDioX0KZEiqkBG9Ze4mk",
                "https://drive.google.com/open?id=1qIEBl4MIo31_XIXrH1W7R05t5t2t1imokaIr5ZbleM4",
                "https://drive.google.com/open?id=1KW6jVCie6Aj1mqvBZQ5UdSDMBGwheUM-KyjgkG_nE-g",
                "https://drive.google.com/open?id=1XqIneddwbgiZaKL6PfvzvFyVvQJsbNkwxJEx8Q9294w",
                "https://drive.google.com/open?id=1WZWaYsqdakzCtPFk2i3vavspP4ZQdpPSHyqZorbjf_8"
        };

        Runner testRunner = createRunner("drones/test");
        Runner normalRunner = createRunner("drones/run");
        initCCC(catalysts, cccLanguages, cccChallengeEndpoint, cccTaskEndpoint, ccc,
                Duration.ofHours(4), "Drones", testRunner, normalRunner);
        Runner schoolTestRunner = createRunner("drones-2d/test");
        Runner schoolNormalRunner = createRunner("drones-2d/run");
        initCCC(catalysts, cccLanguages, cccChallengeEndpoint, cccTaskEndpoint, schoolCCC,
                Duration.ofHours(2), "Drones 2D", schoolTestRunner, schoolNormalRunner);
    }

    private Organization initCatalystsWithUsers() {
        Organization catalysts = new Organization();
        catalysts.setName("Catalysts");
        catalysts.setNick("catalysts");
        catalysts = organizationRepository.save(catalysts);

        String password = this.passwordEncoder.encode("Whatismypassword?");

        List<User> users = new ArrayList<>();
        users.add(createUser("vbalan", "victor.balan@cod.uno", password, true));
        users.add(createUser("pbochis", "paul.bochis@cod.uno", password, true));
        users.add(createUser("flowlo", "lorenz.leutgeb@cod.uno", password, true));
        users.add(createUser("exordian", "jakob.englisch@cod.uno", password, true));
        users.add(createUser("steindl", "cristoph.steindl@catalysts.cc", password, true));

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
                         String[] instructions, Duration duration, String name,
                         Runner testRunner, Runner normalRunner) {

        ChallengeTemplate ccc = new ChallengeTemplate();
        ccc.setName(name);
        ccc.setDescription("## Description");
        ccc.setInstructions("## Instructions");
        ccc.setOrganization(catalysts);
        ccc.setEndpoint(cccChallengeEndpoint);
        ccc.setDuration(duration);

        for (int i = 0; i < instructions.length; i++) {
            Map<String, String> taskParams = new HashMap<>();
            taskParams.put("level", i + 1 + "");
            taskParams.put("test", "1");
            Task task = createTask("Level " + i + 1, "## Description", instructions[i],
                    cccEndpoint, normalRunner, Duration.ofHours(4), catalysts, languages, taskParams);
            for (int j = 1; j <= 3; j++) {
                Map<String, String> params = new HashMap<>();
                params.put("level", i + 1 + "");
                params.put("test", j + "");
                createTest(task, testRunner, params);
            }
            ccc.addTask(task);
        }
        challengeTemplateRepository.save(ccc);
    }

    private Runner createRunner(String name) {
        Runner runner = new Runner();
        runner.setName(name);
        return runnerRepository.save(runner);
    }

    private Endpoint createEndpoint(String name, String component) {
        Endpoint outputMatchEndpoint = new Endpoint();
        outputMatchEndpoint.setName(name);
        outputMatchEndpoint.setComponent(component);
        return endpointRepository.save(outputMatchEndpoint);
    }

    private Task createTask(String name, String description, String instructions, Endpoint endpoint, Runner runner, Duration duration, Set<Language> languages) {
        return createTask(name, description, instructions, endpoint, runner, duration, null, languages, null);
    }

    private Task createTask(String name, String description, String instructions, Endpoint endpoint, Runner runner, Duration duration, Organization organization, Set<Language> languages, Map<String, String> params) {
        Task task = new Task();
        task.setName(name);
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

    private Test createTest(Task task, Runner runner, Map<String, String> params) {
        Test helloWorldTest = new Test();
        helloWorldTest.setTask(task);
        helloWorldTest.setRunner(runner);
        helloWorldTest.setParams(params);
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
        OrganizationMemberKey key = new OrganizationMemberKey();
        key.setUser(user);
        key.setOrganization(organization);

        OrganizationMember membership = new OrganizationMember();
        membership.setKey(key);
        membership.setAdmin(true);
        membership = organizationMemberRepository.save(membership);
    }

    private Language createLanguage(String name, String tag) {
        Language language = new Language();
        language.setName(name);
        language.setTag(tag);
        return languageRepository.save(language);
    }

    private Template createTemplate(Task task, Language language, String fileName) {
        Template helloWorldPyTemplate = new Template();
        helloWorldPyTemplate.setTask(task);
        helloWorldPyTemplate.setLanguage(language);
        helloWorldPyTemplate.setFileName(fileName);
        return templateRepository.save(helloWorldPyTemplate);
    }
}
