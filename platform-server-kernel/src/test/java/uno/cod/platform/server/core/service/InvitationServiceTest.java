package uno.cod.platform.server.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Invitation;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.invitation.InvitationShowDto;
import uno.cod.platform.server.core.exception.CodunoAccessDeniedException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.InvitationRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;
import uno.cod.platform.server.core.service.util.InvitationTestUtil;
import uno.cod.platform.server.core.service.util.ResultTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class InvitationServiceTest {
    private InvitationService invitationService;

    private UserRepository userRepository;
    private InvitationRepository invitationRepository;
    private ResultRepository resultRepository;
    private ChallengeRepository challengeRepository;
    private UserService userService;
    private MailService mailService;
    private GithubService githubService;
    private HttpSession httpSession;

    @Before
    public void setUp() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.invitationRepository = Mockito.mock(InvitationRepository.class);
        this.resultRepository = Mockito.mock(ResultRepository.class);
        this.challengeRepository = Mockito.mock(ChallengeRepository.class);
        this.userService = Mockito.mock(UserService.class);
        this.mailService = Mockito.mock(MailService.class);
        this.githubService = Mockito.mock(GithubService.class);
        this.httpSession = Mockito.mock(HttpSession.class);

        this.invitationService = new InvitationService(userRepository, invitationRepository, resultRepository, challengeRepository, userService, mailService, githubService, httpSession);
    }

    @Test
    public void getByChallengeId() throws Exception {
        Invitation invitation = InvitationTestUtil.getInvitation();
        User user = UserTestUtil.getUser();
        Result result = ResultTestUtil.getResult();

        when(invitationRepository.findAllByChallenge(invitation.getChallenge().getId())).thenReturn(Collections.singletonList(invitation));
        when(userRepository.findByUsernameOrEmail(invitation.getEmail(), invitation.getEmail())).thenReturn(user);
        when(resultRepository.findOneByUserAndChallenge(user.getId(), invitation.getChallenge().getId())).thenReturn(result);

        List<InvitationShowDto> dtos = invitationService.getByChallengeId(invitation.getChallenge().getId());

        assertEquals(dtos.size(), 1);

        InvitationShowDto dto = dtos.get(0);

        assertEquals(dto.getEmail(), invitation.getEmail());
        assertEquals(dto.getUsername(), user.getUsername());
        assertEquals(dto.getToken(), invitation.getToken());
        assertEquals(dto.getStarted(), result.getStarted());
        assertEquals(dto.getExpire(), invitation.getExpire());
    }

    @Test(expected = CodunoAccessDeniedException.class)
    public void authByExpiredToken() throws Exception {
        final String token = "iamexpired";

        final Invitation invitation = new Invitation();

        // Let's hope nobody travels back in time before
        // the seventies and executes this unit test ...
        invitation.setExpire(Instant.EPOCH.atZone(ZoneId.of("UTC")));

        when(invitationRepository.findOne(token)).thenReturn(invitation);

        invitationService.authByToken(token);
    }

    @Test(expected = CodunoAccessDeniedException.class)
    public void authByNonexistentToken() throws Exception {
        final String token = "mimistaccio";

        when(invitationRepository.getOne(token)).thenReturn(null);

        invitationService.authByToken(token);
    }
}