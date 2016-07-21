package uno.cod.platform.server.codingcontest.sync.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.codingcontest.sync.dto.CodingContestGameDto;
import uno.cod.platform.server.codingcontest.sync.dto.PuzzleDto;
import uno.cod.platform.server.codingcontest.sync.dto.PuzzleTestDto;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.*;
import uno.cod.storage.PlatformStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Transactional
public class CatcoderGameImportService {
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final EndpointRepository endpointRepository;
    private final RunnerRepository runnerRepository;
    private final LanguageRepository languageRepository;
    private final TestRepository testRepository;
    private final PlatformStorage storage;
    private final OrganizationRepository organizationRepository;
    private final TaskRepository taskRepository;

    @Value("${coduno.tests.bucket}")
    private String testsBucket;

    @Value("${coduno.instructions.bucket}")
    private String instructionsBucket;

    @Autowired
    public CatcoderGameImportService(ChallengeTemplateRepository challengeTemplateRepository,
                                    EndpointRepository endpointRepository,
                                    RunnerRepository runnerRepository,
                                    LanguageRepository languageRepository,
                                    PlatformStorage storage,
                                    TestRepository testRepository,
                                    OrganizationRepository organizationRepository,
                                    TaskRepository taskRepository) {
        this.challengeTemplateRepository = challengeTemplateRepository;
        this.endpointRepository = endpointRepository;
        this.runnerRepository = runnerRepository;
        this.languageRepository = languageRepository;
        this.storage = storage;
        this.testRepository = testRepository;
        this.organizationRepository = organizationRepository;
        this.taskRepository = taskRepository;
    }

    private ChallengeTemplate mapChallengeTemplate(CodingContestGameDto dto, Organization organization, Duration gameDuration) {
        Endpoint challengeEndpoint = getEndpoint("CCC challenge", "ccc-challenge");

        ChallengeTemplate challengeTemplate = new ChallengeTemplate(fixCanonicalName(dto.getCanonicalName()), dto.getName());
        challengeTemplate.setDescription(dto.getDescription());
        challengeTemplate.setEndpoint(challengeEndpoint);
        challengeTemplate.setOrganization(organization);
        challengeTemplate.setDuration(gameDuration);
        challengeTemplate.setInstructions("You will be presented with a game composed of multiple levels. " +
                "Each level has a description that you can download by pressing the button in the bottom right corner of the screen. " +
                "The input for each level will be read from stdin. Good luck and have fun!");

        return challengeTemplate;
    }

    private Task mapTask(PuzzleDto puzzle,
                         ChallengeTemplate challengeTemplate,
                         Organization organization,
                         Map<String, ByteArrayOutputStream> files,
                         Duration gameDuration,
                         Runner runner,
                         Endpoint endpoint,
                         Set<Language> languages) throws IOException {
        String cn = fixCanonicalName(challengeTemplate.getCanonicalName() + "-" + puzzle.getCanonicalName());
        Task task = new Task(cn, puzzle.getCanonicalName());
        task.setEndpoint(endpoint);
        task.setDescription(puzzle.getCanonicalName());
        task.setInstructions(storage.uploadPublic(instructionsBucket,
                instructionsFileName(challengeTemplate, puzzle.getInstructionsFile()),
                new ByteArrayInputStream(files.get(puzzle.getInstructionsFile()).toByteArray()),
                "application/pdf"));
        task.setDuration(gameDuration);
        task.setRunner(runner);
        task.setLanguages(languages);
        task.setOrganization(organization);
        return task;
    }

    private Test mapTest(PuzzleTestDto puzzleTest, Runner runner, Map<String, ByteArrayOutputStream> testFiles) throws IOException {
        Test test = new Test();
        test.setIndex(Integer.parseInt(puzzleTest.getIndex()));
        test.setRunner(runner);
        test = testRepository.save(test);
        String inputFileName = test.getId() + "/" + puzzleTest.getIndex() + ".txt";
        if (testFiles == null) {
            storage.uploadPublic(testsBucket, inputFileName, new ByteArrayInputStream(puzzleTest.getData().getBytes()), "text/plain");
        } else {
            storage.uploadPublic(testsBucket, inputFileName, new ByteArrayInputStream(testFiles.get(puzzleTest.getData()).toByteArray()), "text/plain");
        }
        storage.upload(testsBucket, test.getId() + "/output.txt", new ByteArrayInputStream(puzzleTest.getSolution().getBytes()), "text/plain");
        Map<String, String> testParams = new HashMap<>();
        testParams.put("test", testsBucket + "/" + test.getId() + "/output.txt");
        testParams.put("stdin", testsBucket + "/" + inputFileName);
        test.setParams(testParams);
        test = testRepository.save(test);
        test.setRunner(runner);
        return test;
    }

    private UUID createChallengeTemplate(CodingContestGameDto dto, UUID organizationId, Map<String, ByteArrayOutputStream> files) throws IOException {
        ChallengeTemplate challengeTemplate = challengeTemplateRepository.findOneByCanonicalName(dto.getCanonicalName());
        if (challengeTemplate != null) {
            return challengeTemplate.getId();
        }
        Organization organization = organizationRepository.findOne(organizationId);
        if (organization == null) {
            throw new CodunoIllegalArgumentException("organization.invalid");
        }

        if (dto.getPuzzles()
                .stream()
                .findAny()
                .filter(puzzleDto -> puzzleDto.getValidationClass() != null)
                .isPresent()) {
            throw new CodunoIllegalArgumentException("ccc.game.structure.unsupported");
        }

        Runner runner = getRunner("/io");
        Endpoint taskEndpoint = getEndpoint("CCC general task", "ccc-io-task");
        Set<Language> languages = new HashSet<>(languageRepository.findAll());
        Duration gameDuration = parseGameDuration(dto.getTimeframe());

        challengeTemplate = mapChallengeTemplate(dto, organization, gameDuration);

        for (PuzzleDto puzzle : dto.getPuzzles()) {
            Task task = mapTask(puzzle, challengeTemplate, organization, files, gameDuration, runner, taskEndpoint, languages);
            Map<String, ByteArrayOutputStream> testFiles = null;
            if (puzzle.getInputFilePath() != null) {
                testFiles = unzip(files.get(puzzle.getInputFilePath()).toByteArray());
            }
            for (PuzzleTestDto puzzleTest : puzzle.getTests()) {
                Test test = mapTest(puzzleTest, runner, testFiles);
                task.addTest(test);
            }
            task = taskRepository.save(task);
            challengeTemplate.addTask(task);
        }
        return challengeTemplateRepository.save(challengeTemplate).getId();
    }

    private String instructionsFileName(ChallengeTemplate challengeTemplate, String fileName) {
        return challengeTemplate.getCanonicalName() + "/" + UUID.randomUUID().toString() + "/" + fileName;
    }

    private Map<String, ByteArrayOutputStream> unzip(byte[] zipbytes) throws IOException {
        final int bufSize = 2048;
        Map<String, ByteArrayOutputStream> map = new HashMap<>();
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(zipbytes));
        ZipEntry entry;
        byte[] buf = new byte[bufSize];
        while ((entry = zip.getNextEntry()) != null) {
            String fileName = entry.getName().substring(entry.getName().indexOf("/") + 1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int count;
            while ((count = zip.read(buf, 0, bufSize)) != -1) {
                baos.write(buf, 0, count);
            }
            map.put(fileName, baos);
        }
        return map;
    }

    public UUID createChallengeTemplateFromGameResources(MultipartFile gameZip, UUID organizationId) throws IOException {
        Map<String, ByteArrayOutputStream> files = unzip(gameZip.getBytes());
        CodingContestGameDto game = null;
        for (String fileName : files.keySet()) {
            if (fileName.endsWith(".xml")) {
                byte[] byts = files.get(fileName).toByteArray();
                ObjectMapper mapper = new XmlMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                game = mapper.readValue(new ByteArrayInputStream(byts), CodingContestGameDto.class);
            }
        }
        if (game == null) {
            throw new CodunoIllegalArgumentException("ccc.game.zip.invalid");
        }
        return createChallengeTemplate(game, organizationId, files);
    }

    private Duration parseGameDuration(String duration) {
        LocalTime time = LocalTime.parse(duration);
        int seconds = time.toSecondOfDay();
        return Duration.ofSeconds(seconds);
    }

    private Runner getRunner(String path) {
        Runner runner = runnerRepository.findOneByPath(path);
        if (runner == null) {
            runner = new Runner();
            runner.setPath(path);
            return runnerRepository.save(runner);
        }
        return runner;
    }

    private Endpoint getEndpoint(String name, String component) {
        Endpoint endpoint = endpointRepository.findOneByComponent(component);
        if (endpoint == null) {
            endpoint = new Endpoint();
            endpoint.setComponent(component);
            endpoint.setName(name);
            return endpointRepository.save(endpoint);
        }
        return endpoint;
    }

    private String fixCanonicalName(String canonicalName) {
        return canonicalName.toLowerCase().replaceAll("[^0-9a-z]", "-").replaceAll("--", "");
    }
}
