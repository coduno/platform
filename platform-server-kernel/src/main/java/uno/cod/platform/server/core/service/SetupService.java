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

    public void init(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setEnabled(true);
        this.userRepository.save(user);
        if (Arrays.asList(this.environment.getActiveProfiles()).contains(Profiles.DEVELOPMENT)) {
            LOGGER.info("initializing development database");
            this.initDevelopmentDatabase();
        }
    }

    private void initDevelopmentDatabase() {
        this.initCoduno();
        initCatalysts();
        Language java = new Language();
        java.setName("Java");
        java.setTag("java");
        java = languageRepository.save(java);
        Language python = new Language();
        python.setName("Python");
        python.setTag("py");
        python = languageRepository.save(python);
        Language javascript = new Language();
        javascript.setName("JavaScript");
        javascript.setTag("js");
        javascript = languageRepository.save(javascript);

        Runner simpleRunner = createRunner("simple");
        Runner diffRunner = createRunner("diff");
        Runner ioRunner = createRunner("io");
        Runner cccTestRunner = createRunner("cccdronetest");
        Runner cccNormalRunner = createRunner("cccdronerun");

        Endpoint outputMatchTaskEndpoint = createEndpoint("Output match", "output-match-task");
        Endpoint javaUnitTestTaskEndpoint = createEndpoint("Java Unit Test", "javaut-task");
        Endpoint robotGameTaskEndpoint = createEndpoint("Robot game", "canvas-game-task");
        Endpoint coderJUnitTaskEndpoint = createEndpoint("User coded JUnit tests", "coder-javaut-task");

        Endpoint sequentialChallengeEndpoint = createEndpoint("Sequential challenge", "sequential-challenge");

        Endpoint cccTaskEndpoint = createEndpoint("CCC drone task", "ccc-drone-task");
        Endpoint cccChallengeEndpoint = createEndpoint("CCC challenge", "ccc-challenge");

        Set<Language> languages = new HashSet<>();
        languages.add(python);
        languages.add(java);
        languages.add(javascript);
        Task helloWorldTask = createTask(
                "Hello, world!", "This is a welcome task to our platform. It is the easiest one so you can learn the ui and the workflow.",
                "Create a program that outputs 'Hello, world!' in a language of your preference.",
                outputMatchTaskEndpoint, simpleRunner, Duration.ofMinutes(30), languages);

        Map<String, String> params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(helloWorldTask, diffRunner, params);

        createTemplate(helloWorldTask, python, "default/app.py");
        createTemplate(helloWorldTask, java, "default/Application.java");

        Task fizzBuzzTask = createTask(
                "Fizz Buzz", "Fizz buzz is a group word game for children to teach them about division.\n" +
                        "Players take turns to count incrementally, replacing any number divisible by three with the word 'fizz',\n" +
                        "and any number divisible by five with the word 'buzz'.",
                "Your job is to create the 'fizzbuzz(int n)' function.\n" +
                        "The n parameter represents the max number to wich you need to generate the fizzbuzz data.\n" +
                        "The output needs to be separated by '\\n'.",
                outputMatchTaskEndpoint, null, Duration.ofMinutes(30), languages
        );

        params = new HashMap<>();
        params.put(Test.PATH, "fizzbuzz-fizzbuzz10^2");
        params.put(Test.STDIN, "fizzbuzz-fizzbuzzin10^2");
        createTest(fizzBuzzTask, ioRunner, params);

        params = new HashMap<>();
        params.put(Test.PATH, "fizzbuzz-fizzbuzz10^3");
        params.put(Test.STDIN, "fizzbuzz-fizzbuzzin10^3");
        createTest(fizzBuzzTask, ioRunner, params);

        createTemplate(fizzBuzzTask, python, "default/app.py");
        createTemplate(fizzBuzzTask, java, "default/Application.java");

        ChallengeTemplate challengeTemplate = new ChallengeTemplate();
        challengeTemplate.setName("Coduno test");
        challengeTemplate.setInstructions("Instructions for Coduno test");
        challengeTemplate.setDescription("Description for Coduno test");
        challengeTemplate.setOrganization(organizationRepository.findByNick("coduno"));
        challengeTemplate.setEndpoint(sequentialChallengeEndpoint);
        challengeTemplate.addTask(helloWorldTask);
        challengeTemplate.addTask(fizzBuzzTask);
        challengeTemplate.setDuration(Duration.ofMinutes(30));
        challengeTemplateRepository.save(challengeTemplate);

        initCCC(organizationRepository.findByNick("coduno"), languages, cccTestRunner, cccNormalRunner, cccChallengeEndpoint, cccTaskEndpoint);
    }

    private void initCoduno() {
        User victor = new User();
        victor.setUsername("vbalan");
        victor.setEmail("victor.balan@cod.uno");
        victor.setPassword(this.passwordEncoder.encode("password"));
        victor.setAdmin(true);
        victor.setEnabled(true);
        victor = userRepository.save(victor);

        Organization coduno = new Organization();
        coduno.setName("Coduno");
        coduno.setNick("coduno");
        coduno = organizationRepository.save(coduno);

        OrganizationMemberKey victorCodunoKey = new OrganizationMemberKey();
        victorCodunoKey.setUser(victor);
        victorCodunoKey.setOrganization(coduno);

        OrganizationMember victorCoduno = new OrganizationMember();
        victorCoduno.setKey(victorCodunoKey);
        victorCoduno.setAdmin(true);
        victorCoduno = organizationMemberRepository.save(victorCoduno);
    }

    private Organization initCatalysts() {
        User victor = new User();
        victor.setUsername("vbalan_catalysts");
        victor.setEmail("victor.balan@catalysts.cc");
        victor.setPassword(this.passwordEncoder.encode("password"));
        victor.setAdmin(true);
        victor.setEnabled(true);
        victor = userRepository.save(victor);

        Organization catalysts = new Organization();
        catalysts.setName("Catalysts");
        catalysts.setNick("catalysts");
        catalysts = organizationRepository.save(catalysts);

        OrganizationMemberKey victorCatalystsKey = new OrganizationMemberKey();
        victorCatalystsKey.setUser(victor);
        victorCatalystsKey.setOrganization(catalysts);

        OrganizationMember victorCatalysts = new OrganizationMember();
        victorCatalysts.setKey(victorCatalystsKey);
        victorCatalysts.setAdmin(true);
        victorCatalysts = organizationMemberRepository.save(victorCatalysts);
        return catalysts;
    }

    private void initCCC(Organization org, Set<Language> languages, Runner cccTestRunner, Runner cccNormalRunner, Endpoint cccChallengeEndpoint, Endpoint cccEndpoint) {

        ChallengeTemplate ccc = new ChallengeTemplate();
        ccc.setName("Catalysts Coding Contest");
        ccc.setDescription("## Description");
        ccc.setInstructions("## Instructions for Catalysts Coding Contest");
        ccc.setOrganization(org);
        ccc.setEndpoint(cccChallengeEndpoint);
        ccc.setDuration(Duration.ofHours(4));

        for (int i = 1; i <= 7; i++) {
            Task task = createTask("Level " + i, "## Description", "## Instructions", cccEndpoint, cccNormalRunner, Duration.ofHours(4), org, languages);
            for (int j = 1; j <= 3; j++) {
                Map<String, String> params = new HashMap<>();
                params.put("level", i + "");
                params.put("test", j + "");
                createTest(task, cccTestRunner, params);
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
        return createTask(name, description, instructions, endpoint, runner, duration, null, languages);
    }

    private Task createTask(String name, String description, String instructions, Endpoint endpoint, Runner runner, Duration duration, Organization organization, Set<Language> languages) {
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
        return taskRepository.save(task);
    }

    private Test createTest(Task task, Runner runner, Map<String, String> params) {
        Test helloWorldTest = new Test();
        helloWorldTest.setTask(task);
        helloWorldTest.setRunner(runner);
        helloWorldTest.setParams(params);
        return testRepository.save(helloWorldTest);
    }

    private Template createTemplate(Task task, Language language, String fileName) {
        Template helloWorldPyTemplate = new Template();
        helloWorldPyTemplate.setTask(task);
        helloWorldPyTemplate.setLanguage(language);
        helloWorldPyTemplate.setFileName(fileName);
        return templateRepository.save(helloWorldPyTemplate);
    }
}
