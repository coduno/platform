package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ChallengeTemplateRepository;
import uno.cod.platform.server.core.service.util.ChallengeTestUtil;

import java.util.UUID;

public class ChallengeServiceTest {
    private ChallengeService service;
    private ChallengeRepository repository;
    private ChallengeTemplateRepository challengeTemplateRepository;
    private ResultService resultService;

    @Before
    public void setup() {
        repository = Mockito.mock(ChallengeRepository.class);
        challengeTemplateRepository = Mockito.mock(ChallengeTemplateRepository.class);
        resultService = Mockito.mock(ResultService.class);
        service = new ChallengeService(repository, challengeTemplateRepository, resultService);
    }

    @Test
    // TODO create mappers for the create functions so we can test the logic.
    // TODO expected vs actual and not just mock vs mock
    public void createFromDto() throws Exception {
        ChallengeCreateDto dto = ChallengeTestUtil.getChallengeCreateDto();
        Challenge challenge = ChallengeTestUtil.getChallenge(dto);
        challenge.setId(UUID.randomUUID());

        Mockito.when(challengeTemplateRepository.findOne(dto.getTemplateId())).thenReturn(challenge.getChallengeTemplate());
        Mockito.when(repository.save(Mockito.any(Challenge.class))).thenReturn(challenge);

        UUID id = service.createFromDto(dto);
        Assert.assertEquals(id, challenge.getId());
    }

    @Test
    public void findOneById() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();

        Mockito.when(repository.findOne(challenge.getId())).thenReturn(challenge);

        ChallengeDto dto = service.findOneById(challenge.getId());

        Assert.assertEquals(dto.getId(), challenge.getId());
        Assert.assertEquals(dto.getName(), challenge.getName());
        Assert.assertEquals(dto.getCanonicalName(), challenge.getCanonicalName());
        Assert.assertEquals(dto.getStartDate(), challenge.getStartDate());
        Assert.assertEquals(dto.getEndDate(), challenge.getEndDate());
    }
}