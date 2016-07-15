package uno.cod.platform.server.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import uno.cod.platform.server.core.domain.ActivationToken;
import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.invitation.InvitationShowDto;
import uno.cod.platform.server.core.exception.CodunoAccessDeniedException;
import uno.cod.platform.server.core.repository.ActivationTokenRepository;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;
import uno.cod.platform.server.core.service.util.ActivationTokenTestUtil;
import uno.cod.platform.server.core.service.util.ResultTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class InvitationServiceTest {
    private InvitationService invitationService;

    private UserRepository userRepository;
    private ActivationTokenRepository activationTokenRepository;
    private ResultRepository resultRepository;
    private ChallengeRepository challengeRepository;
    private MailService mailService;
    private GithubService githubService;
    private HttpSession httpSession;
    private PasswordEncoder passwordEncoder;
    private ActivationTokenService activationTokenService;

    @Before
    public void setUp() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.activationTokenRepository = Mockito.mock(ActivationTokenRepository.class);
        this.resultRepository = Mockito.mock(ResultRepository.class);
        this.challengeRepository = Mockito.mock(ChallengeRepository.class);
        this.mailService = Mockito.mock(MailService.class);
        this.githubService = Mockito.mock(GithubService.class);
        this.httpSession = Mockito.mock(HttpSession.class);
        this.activationTokenService = Mockito.mock(ActivationTokenService.class);
        this.passwordEncoder = Mockito.mock(PasswordEncoder.class);

        this.invitationService = new InvitationService(userRepository, resultRepository, challengeRepository, mailService, githubService, httpSession, activationTokenRepository, activationTokenService, passwordEncoder);
    }

    @Test
    public void getByChallengeId() throws Exception {
        ActivationToken activationToken = ActivationTokenTestUtil.getActivationToken();
        User user = UserTestUtil.getUser();
        Result result = ResultTestUtil.getResult();

        when(activationTokenRepository.findAllByChallenge(activationToken.getChallenge().getId())).thenReturn(Collections.singletonList(activationToken));
        when(userRepository.findByUsernameOrEmail(activationToken.getEmail(), activationToken.getEmail())).thenReturn(user);
        when(resultRepository.findOneByUserAndChallenge(user.getId(), activationToken.getChallenge().getId())).thenReturn(result);

        List<InvitationShowDto> dtos = invitationService.getByChallengeId(activationToken.getChallenge().getId());

        assertEquals(dtos.size(), 1);

        InvitationShowDto dto = dtos.get(0);

        assertEquals(dto.getEmail(), activationToken.getEmail());
        assertEquals(dto.getUsername(), user.getUsername());
        assertEquals(dto.getToken(), activationToken.getToken());
        assertEquals(dto.getStarted(), result.getStarted());
        assertEquals(dto.getExpire(), activationToken.getExpire());
    }

    @Test(expected = CodunoAccessDeniedException.class)
    public void authByExpiredToken() throws Exception {
        final UUID id = UUID.randomUUID();

        final ActivationToken activationToken = new ActivationToken();

        // Let's hope nobody travels back in time before
        // the seventies and executes this unit test ...
        activationToken.setExpire(Instant.EPOCH.atZone(ZoneId.of("UTC")));

        when(activationTokenRepository.findOne(id)).thenReturn(activationToken);

        invitationService.authByToken(new String(Base64.encode((id.toString() + ":x").getBytes())));
    }

    @Test(expected = CodunoAccessDeniedException.class)
    public void authByNonexistentToken() throws Exception {
        final UUID id = UUID.randomUUID();

        when(activationTokenRepository.getOne(id)).thenReturn(null);

        invitationService.authByToken(new String(Base64.encode((id.toString() + ":x").getBytes())));
    }
}