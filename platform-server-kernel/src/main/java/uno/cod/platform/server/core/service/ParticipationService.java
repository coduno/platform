package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.challenge.ParticipationCreateDto;
import uno.cod.platform.server.core.dto.participation.ParticipationShowDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.repository.*;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ParticipationService {
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;
    private final ParticipationInvitationRepository participationInvitationRepository;
    private final LocationRepository locationRepository;
    private final MailService mailService;

    @Autowired
    public ParticipationService(UserRepository userRepository,
                                ChallengeRepository challengeRepository,
                                TeamRepository teamRepository,
                                ParticipationRepository participationRepository,
                                ParticipationInvitationRepository participationInvitationRepository,
                                LocationRepository locationRepository,
                                MailService mailService) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.teamRepository = teamRepository;
        this.participationRepository = participationRepository;
        this.participationInvitationRepository = participationInvitationRepository;
        this.locationRepository = locationRepository;
        this.mailService = mailService;
    }

    public void registerForChallenge(User user, String challengeName) throws MessagingException {
        this.registerForChallenge(user, challengeName, null);
    }

    public void registerForChallenge(User user, String challengeName, ParticipationCreateDto dto) throws MessagingException {
        if (user == null) {
            throw new CodunoIllegalArgumentException("user.invalid");
        }

        Challenge challenge = challengeRepository.findOneByCanonicalName(challengeName);
        if (challenge == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }

        Participation participation = participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId());
        if (participation == null) {
            user = userRepository.getOne(user.getId());
            ParticipationKey key = new ParticipationKey();
            key.setChallenge(challenge);
            key.setUser(user);

            if (user == null) {
                throw new CodunoIllegalArgumentException("user.invalid");
            }

            participation = new Participation();
            participation.setKey(key);

            Map<String, Object> params = new HashMap<>();
            params.put("challengeCanonicalName", challenge.getCanonicalName());
            params.put("challengeName", challenge.getName());
            mailService.sendMail(user.getFullName(), user.getEmail(), "Confirm Registration", "registration-confirmation", params, Locale.ENGLISH);
        }

        if (dto != null && dto.getLocation() != null) {
            participation.setLocation(locationRepository.findOne(dto.getLocation()));
        } else {
            participation.setLocation(null);
        }
        // Join as teamName
        if (dto != null && dto.getTeam() != null && !dto.getTeam().isEmpty()) {
            Team team = teamRepository.findByCanonicalNameAndEnabledTrue(dto.getTeam());
            if (!checkUserInTeam(user, team)) {
                throw new CodunoIllegalArgumentException("team.invalid");
            }
            participation.setTeam(team);
        } else {
            participation.setTeam(null);
        }

        participationRepository.save(participation);

        ParticipationInvitation invitation = participationInvitationRepository.findOneByChallengeAndEmailsContaining(challenge.getId(), user.getEmail());
        if (invitation != null) {
            invitation.getEmails().remove(user.getEmail());
            participationInvitationRepository.save(invitation);
        }
    }

    public void unregisterFromChallenge(User user, String challengeCanonicalName) {
        Challenge challenge = challengeRepository.findOneByCanonicalName(challengeCanonicalName);
        if (challenge == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }
        Participation participation = participationRepository.findOneByUserAndChallenge(user.getId(), challenge.getId());
        if (participation == null) {
            throw new CodunoNoSuchElementException("participation.not.registered");
        }
        participationRepository.delete(participation);
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
