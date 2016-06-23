package uno.cod.platform.server.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.participation.invitation.ParticipationInvitationCreateDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ParticipationInvitationRepository;
import uno.cod.platform.server.core.repository.ParticipationRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import java.util.*;

@Service
public class ParticipationInvitationService {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipationInvitationService.class);

    private final ParticipationInvitationRepository repository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    private final MailService mailService;
    private final TeamInvitationService teamInvitationService;

    @Autowired
    public ParticipationInvitationService(ParticipationInvitationRepository repository, ParticipationRepository participationRepository, UserRepository userRepository, ChallengeRepository challengeRepository, MailService mailService, TeamInvitationService teamInvitationService) {
        this.repository = repository;
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.mailService = mailService;
        this.teamInvitationService = teamInvitationService;
    }

    public void save(ParticipationInvitationCreateDto dto) {
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
        if (participation == null || participation.getTeam() == null) {
            throw new CodunoIllegalArgumentException("participation.invalid");
        }

        Set<String> toInvite = new HashSet<>();
        if (dto.getEmails() == null || dto.getEmails().isEmpty()) {
            throw new CodunoIllegalArgumentException("participation.invitation.invalid");
        }
        for (String email : dto.getEmails()) {
            Map<String, Object> params = new HashMap<>();
            params.put("challenge", challenge.getName());
            params.put("team", participation.getTeam().getName());
            params.put("challengeCanonicalName", challenge.getCanonicalName());
            params.put("teamCanonicalName", participation.getTeam().getCanonicalName());
            params.put("invitedBy", user.getFullName().isEmpty() ? user.getUsername() : user.getFullName());
            params.put("invitedByUsername", user.getUsername());

            User invitedUser = userRepository.findByEmail(email);
            if (invitedUser == null) {
                params.put("name", email);
                toInvite.add(email);
            } else {
                params.put("name", invitedUser.getFullName().isEmpty() ? user.getUsername() : user.getFullName());
                teamInvitationService.create(user, invitedUser, participation.getTeam(), false);
            }
            try {
                mailService.sendMail(email, email, "Invitation to " + challenge.getName() + " on Coduno",
                        "participation-invitation.html", params, Locale.ENGLISH);
            } catch (MessagingException e) {
                LOG.error("invitation mail for not registered user could not be sent", e);
            }
        }

        ParticipationInvitation invitation = repository.findOne(participation.getKey());
        if (invitation == null) {
            invitation = new ParticipationInvitation();
            invitation.setKey(participation.getKey());
        }
        invitation.addEmails(toInvite);
        repository.save(invitation);
    }
}
