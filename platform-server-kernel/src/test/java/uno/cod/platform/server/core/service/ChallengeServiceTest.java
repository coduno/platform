package uno.cod.platform.server.core.service;

import org.junit.Assert;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ChallengeServiceTest {
    private ChallengeService service;
    private ChallengeRepository repository;
    private ChallengeTemplateRepository challengeTemplateRepository;
    private ResultRepository resultRepository;
    private UserRepository userRepository;
    private ParticipationRepository participationRepository;

    @Before
    public void setup() {
        repository = Mockito.mock(ChallengeRepository.class);
        challengeTemplateRepository = Mockito.mock(ChallengeTemplateRepository.class);
        resultRepository = Mockito.mock(ResultRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        participationRepository = Mockito.mock(ParticipationRepository.class);
        service = new ChallengeService(repository, challengeTemplateRepository, resultRepository, userRepository, participationRepository);
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

    @Test
    public void getUserChallenges() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        Result result = ResultTestUtil.getResult();
        result.setFinished(null);
        User user = UserTestUtil.getUser();

        Mockito.when(repository.findAllWithOrganizationAndInvitedUsersAndRegisteredUsers()).thenReturn(Collections.singletonList(challenge));
        Mockito.when(resultRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(result);

        List<UserChallengeShowDto> dtos = service.getPublicChallenges(user);

        Assert.assertEquals(dtos.size(), 1);
        UserChallengeShowDto dto = dtos.get(0);

        Assert.assertEquals(dto.getChallenge().getId(), challenge.getId());
        Assert.assertEquals(dto.getStatus(), UserChallengeShowDto.ChallengeStatus.IN_PROGRESS);
    }
}