package uno.cod.platform.server.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.dto.challenge.UserChallengeShowDto;
import uno.cod.platform.server.core.repository.*;
import uno.cod.platform.server.core.service.util.ChallengeTestUtil;
import uno.cod.platform.server.core.service.util.ResultTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChallengeServiceTest {
    private ChallengeService service;
    private ChallengeRepository repository;
    private ChallengeTemplateRepository challengeTemplateRepository;
    private ResultRepository resultRepository;
    private LocationRepository locationRepository;
    private LocationDetailRepository locationDetailRepository;

    @Before
    public void setup() {
        repository = mock(ChallengeRepository.class);
        challengeTemplateRepository = mock(ChallengeTemplateRepository.class);
        resultRepository = mock(ResultRepository.class);
        locationRepository = mock(LocationRepository.class);
        locationDetailRepository = mock(LocationDetailRepository.class);
        service = new ChallengeService(repository, challengeTemplateRepository, resultRepository, locationRepository, locationDetailRepository);
    }

    @Test
    // TODO create mappers for the create functions so we can test the logic.
    // TODO expected vs actual and not just mock vs mock
    public void createFromDto() throws Exception {
        ChallengeCreateDto dto = ChallengeTestUtil.getChallengeCreateDto();
        Challenge challenge = ChallengeTestUtil.getChallenge(dto);

        when(challengeTemplateRepository.findOneByCanonicalName(dto.getTemplateCanonicalName())).thenReturn(challenge.getChallengeTemplate());
        when(repository.save(Mockito.any(Challenge.class))).thenReturn(challenge);

        String canonicalName = service.createFromDto(dto);
        assertEquals(canonicalName, challenge.getCanonicalName());
    }

    @Test
    public void findOneById() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();

        when(repository.findOne(challenge.getId())).thenReturn(challenge);

        ChallengeDto dto = service.findOneById(challenge.getId());

        assertEquals(dto.getId(), challenge.getId());
        assertEquals(dto.getName(), challenge.getName());
        assertEquals(dto.getCanonicalName(), challenge.getCanonicalName());
        assertEquals(dto.getStartDate(), challenge.getStartDate());
        assertEquals(dto.getEndDate(), challenge.getEndDate());
    }

    @Test
    public void getUserChallenges() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        challenge.setEndDate(ZonedDateTime.now().plusDays(5));
        Result result = ResultTestUtil.getResult();
        result.setFinished(null);
        User user = UserTestUtil.getUser();

        when(repository.findAllByInvitedUser(user.getId())).thenReturn(Collections.singletonList(challenge));
        when(resultRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(result);

        List<UserChallengeShowDto> dtos = service.getInviteOnlyChallenges(user);

        assertEquals(dtos.size(), 1);
        UserChallengeShowDto dto = dtos.get(0);

        assertEquals(dto.getChallenge().getId(), challenge.getId());
        assertEquals(dto.getStatus(), UserChallengeShowDto.ChallengeStatus.IN_PROGRESS);
    }
}