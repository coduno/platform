package uno.cod.platform.server.core.service;

import org.junit.Before;
import org.junit.Test;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.Participation;
import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.*;
import uno.cod.platform.server.core.service.mail.MailService;
import uno.cod.platform.server.core.service.util.ChallengeTestUtil;
import uno.cod.platform.server.core.service.util.ParticipationUtil;
import uno.cod.platform.server.core.service.util.TeamTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParticipationServiceTest {
    ParticipationService service;
    private UserRepository userRepository;
    private ChallengeRepository challengeRepository;
    private TeamRepository teamRepository;
    private ParticipationRepository participationRepository;

    @Before
    public void setUp() throws Exception {
        this.userRepository = mock(UserRepository.class);
        this.challengeRepository = mock(ChallengeRepository.class);
        this.teamRepository = mock(TeamRepository.class);
        this.participationRepository = mock(ParticipationRepository.class);
        ParticipationInvitationRepository participationInvitationRepository = mock(ParticipationInvitationRepository.class);
        LocationRepository locationRepository = mock(LocationRepository.class);
        MailService mailService = mock(MailService.class);
        this.service = new ParticipationService(userRepository, challengeRepository, teamRepository, participationRepository, participationInvitationRepository, locationRepository, mailService);
    }

    @Test
    public void registerForChallengeWithTeam() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Team team = TeamTestUtil.getTeamWithMember(user);
        when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(null);
        when(teamRepository.findByCanonicalNameAndEnabledTrue(team.getCanonicalName())).thenReturn(team);
        when(userRepository.getOne(user.getId())).thenReturn(user);

        service.registerForChallenge(user, challenge.getCanonicalName(), ParticipationUtil.getCreateDto(team.getCanonicalName()));
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void registerForChallengeWithTeamNotExisting() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Team team = TeamTestUtil.getTeamWithMember(user);
        when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(null);
        when(teamRepository.findByCanonicalNameAndEnabledTrue(team.getCanonicalName())).thenReturn(null);

        service.registerForChallenge(user, challenge.getCanonicalName(), ParticipationUtil.getCreateDto(team.getCanonicalName()));
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void registerForChallengeWithTeamNotMember() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Team team = TeamTestUtil.getTeamWithMember(UserTestUtil.getUser("random", "random"));
        when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(null);
        when(teamRepository.findByCanonicalNameAndEnabledTrue(team.getCanonicalName())).thenReturn(team);
        when(userRepository.getOne(user.getId())).thenReturn(user);

        service.registerForChallenge(user, challenge.getCanonicalName(), ParticipationUtil.getCreateDto(team.getCanonicalName()));
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void registerForChallengeExistingParticipation() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(new Participation(challenge, user));

        service.registerForChallenge(user, challenge.getCanonicalName(), ParticipationUtil.getCreateDto("team"));
    }
}