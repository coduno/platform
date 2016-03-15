package uno.cod.platform.server.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.Profiles;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.repository.*;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class SetupService {
    static final Logger LOGGER = LoggerFactory.getLogger(SetupService.class);
    private static final String TAG = SetupService.class.getSimpleName();
    private Environment environment;
    private JdbcTemplate jdbcTemplate;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private final EndpointRepository endpointRepository;
    private final TaskRepository taskRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final RunnerRepository runnerRepository;
    private final TestRepository testRepository;
    private final TemplateRepository templateRepository;

    @Autowired
    public SetupService(Environment environment, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, UserRepository userRepository, EndpointRepository endpointRepository, TaskRepository taskRepository, OrganizationRepository organizationRepository, OrganizationMemberRepository organizationMemberRepository, ChallengeRepository challengeRepository, RunnerRepository runnerRepository, TestRepository testRepository, TemplateRepository templateRepository) {
        this.environment = environment;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.endpointRepository = endpointRepository;
        this.taskRepository = taskRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.challengeRepository = challengeRepository;
        this.runnerRepository = runnerRepository;
        this.testRepository = testRepository;
        this.templateRepository = templateRepository;
    }

    public void init(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setEnabled(true);
        this.userRepository.save(user);
        if(Arrays.asList(this.environment.getActiveProfiles()).contains(Profiles.DEVELOPMENT)) {
            LOGGER.info("initializing development database");
            this.initDevelopmentDatabase();
        }
    }

    private void initDevelopmentDatabase() {
        this.initCoduno();
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

        Task helloWorldTask = createTask(
                "Hello, world!", "This is a welcome task to our platform. It is the easiest one so you can learn the ui and the workflow.",
                "Create a program that outputs 'Hello, world!' in a language of your preference.",
                outputMatchTaskEndpoint, simpleRunner, Duration.ofMinutes(30));

        Map<String, String> params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(helloWorldTask, diffRunner, params);

        createTemplate(helloWorldTask, Language.PYTHON, "default/app.py");
        createTemplate(helloWorldTask, Language.JAVA, "default/Application.java");

        Task fizzBuzzTask = createTask(
                "Fizz Buzz", "Fizz buzz is a group word game for children to teach them about division.\n" +
                        "Players take turns to count incrementally, replacing any number divisible by three with the word 'fizz',\n" +
                        "and any number divisible by five with the word 'buzz'.",
                "Your job is to create the 'fizzbuzz(int n)' function.\n" +
                        "The n parameter represents the max number to wich you need to generate the fizzbuzz data.\n" +
                        "The output needs to be separated by '\\n'.",
                outputMatchTaskEndpoint, null, Duration.ofMinutes(30)
        );

        params = new HashMap<>();
        params.put(Test.PATH, "fizzbuzz-fizzbuzz10^2");
        params.put(Test.STDIN, "fizzbuzz-fizzbuzzin10^2");
        createTest(fizzBuzzTask, ioRunner, params);

        params = new HashMap<>();
        params.put(Test.PATH, "fizzbuzz-fizzbuzz10^3");
        params.put(Test.STDIN, "fizzbuzz-fizzbuzzin10^3");
        createTest(fizzBuzzTask, ioRunner, params);

        createTemplate(fizzBuzzTask, Language.PYTHON, "default/app.py");
        createTemplate(fizzBuzzTask, Language.JAVA, "default/Application.java");

        Challenge challenge = new Challenge();
        challenge.setName("Coduno test");
        challenge.setInstructions("Instructions for Coduno test");
        challenge.setDescription("Description for Coduno test");
        challenge.setOrganization(organizationRepository.findByNick("coduno"));
        challenge.setEndpoint(sequentialChallengeEndpoint);
        challenge.addTask(helloWorldTask);
        challenge.addTask(fizzBuzzTask);
        challenge.setDuration(Duration.ofMinutes(30));
        challengeRepository.save(challenge);


        initCCC(cccTestRunner, cccNormalRunner, cccChallengeEndpoint, cccTaskEndpoint);
    }

    private void initCoduno(){
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

    private Organization initCatalysts(){
        User victor = new User();
        victor.setUsername("catavbalan");
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

    private void initCCC(Runner cccTestRunner,Runner cccNormalRunner, Endpoint cccChallengeEndpoint, Endpoint cccEndpoint){
        Organization catalysts = initCatalysts();

        Task levelOne = createTask("Level 1", "## Description", "## Instructions", cccEndpoint, cccNormalRunner, Duration.ofHours(4), catalysts);
        Map<String, String> params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(levelOne, cccTestRunner, params);

        Task levelTwo = createTask("Level 2", "## Description", "## Instructions", cccEndpoint, cccNormalRunner, Duration.ofHours(4), catalysts);
        params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(levelTwo, cccTestRunner, params);

        Task levelThree = createTask("Level 3", "## Description", "## Instructions", cccEndpoint, cccNormalRunner, Duration.ofHours(4), catalysts);
        params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(levelThree, cccTestRunner, params);;

        Task levelFour = createTask("Level 4", "## Description", "## Instructions", cccEndpoint, cccNormalRunner, Duration.ofHours(4), catalysts);
        params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(levelFour, cccTestRunner, params);

        Task levelFive = createTask("Level 5", "## Description", "## Instructions", cccEndpoint, cccNormalRunner, Duration.ofHours(4), catalysts);
        params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(levelFive, cccTestRunner, params);

        Task levelSix = createTask("Level 6", "## Description", "## Instructions", cccEndpoint, cccNormalRunner, Duration.ofHours(4), catalysts);
        params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(levelSix, cccTestRunner, params);

        Task levelSeven = createTask("Level 7", "## Description", "## Instructions", cccEndpoint, cccNormalRunner, Duration.ofHours(4), catalysts);
        params = new HashMap<>();
        params.put(Test.PATH, "helloworld/helloworld");
        createTest(levelSeven, cccTestRunner, params);

        Challenge ccc = new Challenge();
        ccc.setName("Catalysts Coding Contest");
        ccc.setDescription("## Description");
        ccc.setInstructions("## Instructions for Catalysts Coding Contest");
        ccc.setOrganization(catalysts);
        ccc.setEndpoint(cccChallengeEndpoint);
        ccc.addTask(levelOne);
        ccc.addTask(levelTwo);
        ccc.addTask(levelThree);
        ccc.addTask(levelFour);
        ccc.addTask(levelFive);
        ccc.addTask(levelSix);
        ccc.addTask(levelSeven);
        ccc.setDuration(Duration.ofHours(4));
        challengeRepository.save(ccc);

    }

    private Runner createRunner(String name){
        Runner runner = new Runner();
        runner.setName(name);
        return runnerRepository.save(runner);
    }

    private Endpoint createEndpoint(String name, String component){
        Endpoint outputMatchEndpoint = new Endpoint();
        outputMatchEndpoint.setName(name);
        outputMatchEndpoint.setComponent(component);
        return endpointRepository.save(outputMatchEndpoint);
    }

    private Task createTask(String name, String description, String instructions, Endpoint endpoint, Runner runner, Duration duration){
        return createTask(name, description, instructions, endpoint, runner, duration, null);
    }

    private Task createTask(String name, String description, String instructions, Endpoint endpoint, Runner runner, Duration duration, Organization organization){
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setInstructions(instructions);
        task.setEndpoint(endpoint);
        task.setDuration(duration);
        task.setRunner(runner);
        task.addSkill(CodingSkill.CODING_SPEED, 1D);
        task.setOrganization(organization);
        return taskRepository.save(task);
    }

    private Test createTest(Task task, Runner runner, Map<String, String> params){
        Test helloWorldTest = new Test();
        helloWorldTest.setTask(task);
        helloWorldTest.setRunner(runner);
        helloWorldTest.setParams(params);
        return testRepository.save(helloWorldTest);
    }

    private Template createTemplate(Task task, Language language, String fileName){
        Template helloWorldPyTemplate = new Template();
        helloWorldPyTemplate.setTask(task);
        helloWorldPyTemplate.setLanguage(language);
        helloWorldPyTemplate.setFileName(fileName);
        return templateRepository.save(helloWorldPyTemplate);
    }
}
