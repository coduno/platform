package uno.cod.platform.server.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.challenge.ParticipationCreateDto;
import uno.cod.platform.server.core.dto.participation.ParticipationShowDto;
import uno.cod.platform.server.core.dto.participation.invitation.ParticipationInvitationCreateDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ParticipationInvitationRepository;
import uno.cod.platform.server.core.repository.ParticipationRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class ParticipationInvitationService {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipationInvitationService.class);

    private final ParticipationInvitationRepository repository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    private final MailService mailService;
    private final TeamInvitationService teamInvitationService;
    private final ParticipationService participationService;

    @Autowired
    public ParticipationInvitationService(ParticipationInvitationRepository repository, ParticipationRepository participationRepository, UserRepository userRepository, ChallengeRepository challengeRepository, MailService mailService, TeamInvitationService teamInvitationService, ParticipationService participationService) {
        this.repository = repository;
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.mailService = mailService;
        this.teamInvitationService = teamInvitationService;
        this.participationService = participationService;
    }

    public void save(ParticipationInvitationCreateDto dto) throws MessagingException {
        User user = userRepository.findOne(dto.getUserId());
        if (user == null) {
            throw new CodunoIllegalArgumentException("user.invalid");
        }
        Challenge challenge = challengeRepository.findOne(dto.getChallengeId());
        if (challenge == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }
        ParticipationKey key = new ParticipationKey();
        key.setUser(user);
        key.setChallenge(challenge);
        Participation participation = participationRepository.findOne(key);
        if (participation == null || !participation.isTeam()) {
            throw new CodunoIllegalArgumentException("participation.invalid");
        }

        if (dto.getEmails() == null || dto.getEmails().isEmpty()) {
            throw new CodunoIllegalArgumentException("participation.invitation.invalid");
        }
        for (String email : dto.getEmails()) {
            Map<String, Object> params = new HashMap<>();
            params.put("challenge", challenge.getName());
            params.put("team", participation.getTeam().getName());
            params.put("challengeCanonicalName", challenge.getCanonicalName());
            params.put("teamCanonicalName", participation.getTeam().getCanonicalName());
            params.put("invitedBy", user.getName());
            params.put("invitedByUsername", user.getUsername());

            User invitedUser = userRepository.findByEmail(email);
            if (invitedUser == null) {
                params.put("name", email);
            } else {
                params.put("name", invitedUser.getName());
                teamInvitationService.create(user, invitedUser, participation.getTeam(), false);
            }
            try {
                mailService.sendMail(email, email, "Invitation to " + challenge.getName() + " on Coduno",
                        "participation-invitation", params, Locale.ENGLISH);
            } catch (MessagingException e) {
                LOG.error("invitation mail for not registered user could not be sent", e);
            }
        }

        ParticipationInvitation invitation = repository.findOne(participation.getKey());
        if (invitation == null) {
            invitation = new ParticipationInvitation();
            invitation.setKey(participation.getKey());
        }
        invitation.addEmails(dto.getEmails());
        repository.save(invitation);
    }

    public ParticipationShowDto get(String username, String challenge) {
        Participation participation = participationRepository.findOneByUserCanonicalNameAndChallengeCanonicalName(username, challenge);
        if (participation == null) {
            throw new CodunoIllegalArgumentException("participation.invalid");
        }
        return new ParticipationShowDto(participation);
    }

    public void accept(User user, String username, String challenge) throws MessagingException {
        Participation participation = participationRepository.findOneByUserCanonicalNameAndChallengeCanonicalName(username, challenge);
        if (participation == null) {
            throw new CodunoIllegalArgumentException("participation.invalid");
        }
        ParticipationInvitation invitation = repository.findOne(participation.getKey());
        if (invitation == null || !invitation.getEmails().contains(user.getEmail())) {
            throw new CodunoIllegalArgumentException("participation.invitation.invalid");
        }
        teamInvitationService.acceptInvitation(user, participation.getTeam().getCanonicalName(), false);

        ParticipationCreateDto dto = new ParticipationCreateDto();
        dto.setLocation(participation.getLocation() != null ? participation.getLocation().getId() : null);
        dto.setTeam(participation.getTeam().getCanonicalName());
        participationService.registerForChallenge(user, challenge, dto);

        invitation.getEmails().remove(user.getEmail());
        repository.save(invitation);
    }
}
