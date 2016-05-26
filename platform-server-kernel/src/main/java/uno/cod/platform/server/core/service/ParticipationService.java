package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ParticipationRepository;
import uno.cod.platform.server.core.repository.TeamRepository;
import uno.cod.platform.server.core.repository.UserRepository;

@Service
@Transactional
public class ParticipationService {
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;

    @Autowired
    public ParticipationService(UserRepository userRepository,
                                ChallengeRepository challengeRepository,
                                TeamRepository teamRepository,
                                ParticipationRepository participationRepository) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.teamRepository = teamRepository;
        this.participationRepository = participationRepository;
    }

    public void registerForChallenge(User user, String challengeName) {
        this.registerForChallenge(user, challengeName, null);
    }

    public void registerForChallenge(User user, String challengeName, String teamName) {
        Challenge challenge = challengeRepository.findOneByCanonicalName(challengeName);
        if (challenge == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }
        if (participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId()) != null) {
            throw new CodunoIllegalArgumentException("participation.registered.already");
        }
        user = userRepository.getOne(user.getId());
        ParticipationKey key = new ParticipationKey();
        key.setChallenge(challenge);
        key.setUser(user);

        Participation participation = new Participation();
        participation.setKey(key);
        // Join as teamName
        if (teamName != null && !teamName.isEmpty()) {
            Team team = teamRepository.findByCanonicalNameAndEnabledTrue(teamName);
            if (!checkUserInTeam(user, team)) {
                throw new CodunoIllegalArgumentException("team.invalid");
            }
            participation.setTeam(team);
        }

        participationRepository.save(participation);
    }

    private boolean checkUserInTeam(User user, Team team) {
        if (team == null || user == null) {
            return false;
        }

        for (TeamMember member : team.getMembers()) {
            if (member.getKey().getUser().getId().equals(user.getId())) {
                return true;
            }
        }

        return false;
    }
}
