package uno.cod.platform.server.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.Participation;
import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ParticipationRepository;
import uno.cod.platform.server.core.repository.TeamRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.util.ChallengeTestUtil;
import uno.cod.platform.server.core.service.util.TeamTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

public class ParticipationServiceTest {
    ParticipationService service;
    private UserRepository userRepository;
    private ChallengeRepository challengeRepository;
    private TeamRepository teamRepository;
    private ParticipationRepository participationRepository;

    @Before
    public void setUp() throws Exception {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.challengeRepository = Mockito.mock(ChallengeRepository.class);
        this.teamRepository = Mockito.mock(TeamRepository.class);
        this.participationRepository = Mockito.mock(ParticipationRepository.class);
        this.service = new ParticipationService(userRepository, challengeRepository, teamRepository, participationRepository);
    }

    @Test
    public void registerForChallengeNoTeam() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Mockito.when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        Mockito.when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(null);
        Mockito.when(userRepository.getOne(user.getId())).thenReturn(user);

        service.registerForChallenge(user, challenge.getCanonicalName());
    }

    @Test
    public void registerForChallengeWithTeam() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Team team = TeamTestUtil.getTeamWithMember(user);
        Mockito.when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        Mockito.when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(null);
        Mockito.when(teamRepository.findByCanonicalNameAndEnabledTrue(team.getCanonicalName())).thenReturn(team);
        Mockito.when(userRepository.getOne(user.getId())).thenReturn(user);

        service.registerForChallenge(user, challenge.getCanonicalName(), team.getCanonicalName());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void registerForChallengeWithTeamNotExisting() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Team team = TeamTestUtil.getTeamWithMember(user);
        Mockito.when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        Mockito.when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(null);
        Mockito.when(teamRepository.findByCanonicalNameAndEnabledTrue(team.getCanonicalName())).thenReturn(null);

        service.registerForChallenge(user, challenge.getCanonicalName(), team.getCanonicalName());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void registerForChallengeWithTeamNotMember() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Team team = TeamTestUtil.getTeamWithMember(UserTestUtil.getUser("random", "random"));
        Mockito.when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        Mockito.when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(null);
        Mockito.when(teamRepository.findByCanonicalNameAndEnabledTrue(team.getCanonicalName())).thenReturn(team);
        Mockito.when(userRepository.getOne(user.getId())).thenReturn(user);

        service.registerForChallenge(user, challenge.getCanonicalName(), team.getCanonicalName());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void registerForChallengeNoChallenge() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Mockito.when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(null);

        service.registerForChallenge(user, challenge.getCanonicalName());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void registerForChallengeExistingParticipation() throws Exception {
        Challenge challenge = ChallengeTestUtil.getChallenge();
        User user = UserTestUtil.getUser();
        Mockito.when(challengeRepository.findOneByCanonicalName(challenge.getCanonicalName())).thenReturn(challenge);
        Mockito.when(participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId())).thenReturn(new Participation());

        service.registerForChallenge(user, challenge.getCanonicalName(), "team");
    }
}