package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.challenge.ParticipationCreateDto;
import uno.cod.platform.server.core.dto.participation.ParticipationShowDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.*;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ParticipationService {
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public ParticipationService(UserRepository userRepository,
                                ChallengeRepository challengeRepository,
                                TeamRepository teamRepository,
                                ParticipationRepository participationRepository,
                                LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.teamRepository = teamRepository;
        this.participationRepository = participationRepository;
        this.locationRepository = locationRepository;
    }

    public void registerForChallenge(User user, String challengeName) {
        this.registerForChallenge(user, challengeName, null);
    }

    public void registerForChallenge(User user, String challengeName, ParticipationCreateDto dto) {
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
        if (dto != null && dto.getLocation() != null) {
            participation.setLocation(locationRepository.findOne(dto.getLocation()));
        }
        // Join as teamName
        if (dto != null && dto.getTeam() != null && !dto.getTeam().isEmpty()) {
            Team team = teamRepository.findByCanonicalNameAndEnabledTrue(dto.getTeam());
            if (!checkUserInTeam(user, team)) {
                throw new CodunoIllegalArgumentException("team.invalid");
            }
            participation.setTeam(team);
        }

        participationRepository.save(participation);
    }

    public Set<ParticipationShowDto> getByChallengeCanonicalName(String canonicalName) {
        return participationRepository.findAllByChallengeCanonicalName(canonicalName).stream().map(ParticipationShowDto::new).collect(Collectors.toSet());
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
