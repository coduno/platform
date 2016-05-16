package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Invitation;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.invitation.InvitationShowDto;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.InvitationRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;
import uno.cod.platform.server.core.service.util.InvitationTestUtil;
import uno.cod.platform.server.core.service.util.ResultTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import java.util.Collections;
import java.util.List;

public class InvitationServiceTest {
    private InvitationService invitationService;

    private UserRepository userRepository;
    private InvitationRepository invitationRepository;
    private ResultRepository resultRepository;
    private ChallengeRepository challengeRepository;
    private UserService userService;
    private MailService mailService;
    private GithubService githubService;

    @Before
    public void setUp() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.invitationRepository = Mockito.mock(InvitationRepository.class);
        this.resultRepository = Mockito.mock(ResultRepository.class);
        this.challengeRepository = Mockito.mock(ChallengeRepository.class);
        this.userService = Mockito.mock(UserService.class);
        this.mailService = Mockito.mock(MailService.class);
        this.githubService = Mockito.mock(GithubService.class);

        this.invitationService = new InvitationService(userRepository, invitationRepository, resultRepository, challengeRepository, userService, mailService, githubService);
    }

    @Test
    public void getByChallengeId() throws Exception {
        Invitation invitation = InvitationTestUtil.getInvitation();
        User user = UserTestUtil.getUser();
        Result result = ResultTestUtil.getResult();

        Mockito.when(invitationRepository.findAllByChallenge(invitation.getChallenge().getId())).thenReturn(Collections.singletonList(invitation));
        Mockito.when(userRepository.findByUsernameValueOrEmail(invitation.getEmail(), invitation.getEmail())).thenReturn(user);
        Mockito.when(resultRepository.findOneByUserAndChallenge(user.getId(), invitation.getChallenge().getId())).thenReturn(result);

        List<InvitationShowDto> dtos = invitationService.getByChallengeId(invitation.getChallenge().getId());

        Assert.assertEquals(dtos.size(), 1);

        InvitationShowDto dto = dtos.get(0);

        Assert.assertEquals(dto.getEmail(), invitation.getEmail());
        Assert.assertEquals(dto.getUsername(), user.getUsername());
        Assert.assertEquals(dto.getToken(), invitation.getToken());
        Assert.assertEquals(dto.getStarted(), result.getStarted());
        Assert.assertEquals(dto.getExpire(), invitation.getExpire());
    }
}