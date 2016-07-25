package uno.cod.platform.server.codingcontest.sync.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.*;
import org.springframework.security.crypto.codec.Base64;
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

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Transactional
public class CatcoderGameImportService {
    private final static Logger LOG = LoggerFactory.getLogger(CatcoderGameImportService.class);

    private static final Predicate<String> IS_XML_FILENAME = s -> s.endsWith(".xml");

    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final EndpointRepository endpointRepository;
    private final RunnerRepository runnerRepository;
    private final LanguageRepository languageRepository;
    private final TestRepository testRepository;
    private final PlatformStorage storage;
    private final OrganizationRepository organizationRepository;
    private final TaskRepository taskRepository;
    private final Random random;

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
        this.random = new Random();
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
                         Map<String, byte[]> files,
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
                new ByteArrayInputStream(files.get(puzzle.getInstructionsFile())),
                "application/pdf"));
        task.setDuration(gameDuration);
        task.setRunner(runner);
        task.setLanguages(languages);
        task.setOrganization(organization);
        return task;
    }

    private Test mapTest(PuzzleTestDto puzzleTest, Runner runner, Map<String, byte[]> testFiles) throws IOException {
        Test test = new Test();
        test.setIndex(Integer.parseInt(puzzleTest.getIndex()));
        test.setRunner(runner);
        test = testRepository.save(test);
        String inputFileName = test.getId() + "/" + puzzleTest.getIndex() + ".txt";
        if (testFiles == null) {
            storage.uploadPublic(testsBucket, inputFileName, new ByteArrayInputStream(puzzleTest.getData().getBytes()), "text/plain");
        } else {
            storage.uploadPublic(testsBucket, inputFileName, new ByteArrayInputStream(testFiles.get(puzzleTest.getData())), "text/plain");
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

    private UUID createChallengeTemplate(CodingContestGameDto dto, UUID organizationId, Map<String, byte[]> files) throws IOException {
        ChallengeTemplate challengeTemplate = challengeTemplateRepository.findOneByCanonicalName(dto.getCanonicalName());
        if (challengeTemplate != null) {
            return challengeTemplate.getId();
        }

        Organization organization = organizationRepository.findOne(organizationId);
        if (organization == null) {
            throw new CodunoIllegalArgumentException("organization.invalid");
        }

        if (dto.getPuzzles()
                .parallelStream()
                .filter(x -> x.getValidationClass() != null)
                .findAny()
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
            Map<String, byte[]> testFiles = null;
            if (puzzle.getInputFilePath() != null) {
                try (InputStream is = new ByteArrayInputStream(files.get(puzzle.getInputFilePath()))) {
                    testFiles = unzip(is);
                }
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
        // 33 mod 3 = 0, s.t. Base64 needs no padding.
        final byte[] buffer = new byte[33];
        random.nextBytes(buffer);
        final String random = new String(Base64.encode(buffer)).replace("/", "_").replace("+", "_");

        return challengeTemplate.getCanonicalName() + "/" + random + "/" + fileName;
    }

    /**
     * Unzips a ZIP file so that it's content is accessible without
     * seeking at the expense of keeping the whole file in memory.
     *
     * Entries are keyed by their name according to {@link ZipEntry#getName()}.
     *
     * @param is the input stream to unzip.
     * @return a map that translates an entry name to the byte content of the file.
     * @throws IOException if reading from the input stream fails.
     */
    private Map<String, byte[]> unzip(InputStream is) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(is)) {
            Map<String, byte[]> map = new HashMap<>();
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                final byte[] buffer = new byte[(int) entry.getSize()];

                // NOTE: dis is not wrapped by a try-with-resources block
                // on purpose, because that would imply it being closed
                // when leaving the try block, effectively closing the
                // underlying zis, failing to read further entries.

                DataInputStream dis = new DataInputStream(zis);
                dis.readFully(buffer);
                map.put(entry.getName(), buffer);
            }
            return map;
        }
    }

    public UUID createChallengeTemplateFromGameResources(MultipartFile file, UUID organizationId) throws IOException {
        final Map<String, byte[]> files;

        try (InputStream is = file.getInputStream()) {
            files = unzip(is);
        }

        Optional<CodingContestGameDto> game = files
                .keySet()
                .parallelStream()
                .filter(IS_XML_FILENAME)
                .flatMap(k -> {
                    try {
                        return Stream.of(map(files.get(k)));
                    } catch (Throwable t) {
                        LOG.warn("Deserialization of {} failed.", k, t);
                        return Stream.empty();
                    }
                })
                .findAny();

        if (!game.isPresent()) {
            throw new CodunoIllegalArgumentException("ccc.game.zip.invalid");
        }

        return createChallengeTemplate(game.get(), organizationId, files);
    }

    private static CodingContestGameDto map(byte[] bytes) throws IOException {
        final ObjectMapper mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(new ByteArrayInputStream(bytes), CodingContestGameDto.class);
    }

    /**
     * Converts conventional notation of a duration, e.g. "4:00" for "four hours"
     * to a duration. {@link Duration#parse(CharSequence)} cannot be used because
     * it expects ISO8601 conform input and not the colloquial form above.
     * @param duration the {@link CharSequence} to be parsed.
     * @return parsed representation of the duration.
     */
    private static Duration parseGameDuration(CharSequence duration) {
        return Duration.ofSeconds(LocalTime.parse(duration).toSecondOfDay());
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

    private static String fixCanonicalName(String canonicalName) {
        return canonicalName.toLowerCase().replaceAll("[^0-9a-z]", "-").replaceAll("--", "");
    }
}
