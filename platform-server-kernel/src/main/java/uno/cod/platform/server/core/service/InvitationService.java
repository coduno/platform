package uno.cod.platform.server.core.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.invitation.InvitationDto;
import uno.cod.platform.server.core.dto.invitation.InvitationShowDto;
import uno.cod.platform.server.core.dto.user.UserCreateDto;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.InvitationRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class InvitationService {
    @Value("#{T(java.time.Duration).parse('${coduno.invite.expire}')}")
    private Duration duration;

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final ResultRepository resultRepository;

    private final ChallengeRepository challengeRepository;
    private final MailService mailService;
    private final UserService userService;

    private Logger log = Logger.getLogger(InvitationService.class.getName());
    private final Random random = new Random();

    @Autowired
    public InvitationService(UserRepository userRepository,
                             InvitationRepository invitationRepository,
                             ResultRepository resultRepository, ChallengeRepository challengeRepository,
                             UserService userService,
                             MailService mailService) {
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.resultRepository = resultRepository;
        this.challengeRepository = challengeRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    public void invite(InvitationDto dto, String from) throws MessagingException {
        User invitingUser = userRepository.findByUsername(from);
        Challenge challenge = challengeRepository.findOne(dto.getChallengeId());

        Organization organization = challenge.getChallengeTemplate().getOrganization();
        boolean ok = false;
        for (OrganizationMember organizationMember : invitingUser.getOrganizations()) {
            if (organizationMember.isAdmin() && organizationMember.getKey().getOrganization().getId().equals(organization.getId())) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new AccessDeniedException("you are not an admin to the parent organization of the challenge");
        }

        String token = new BigInteger(130, random).toString(32);

        Invitation invitation = new Invitation();
        invitation.setChallenge(challenge);
        invitation.setEmail(dto.getEmail());
        if (challenge.getStartDate() != null) {
            invitation.setExpire(challenge.getStartDate().plus(challenge.getChallengeTemplate().getDuration()));
        } else {
            invitation.setExpire(ZonedDateTime.now().plus(duration));
        }
        invitation.setToken(token);

        invitationRepository.save(invitation);

        Map<String, Object> params = new HashMap<>();
        params.put("organization", organization.getName());
        params.put("token", token);
        params.put("startDate", challenge.getStartDate());
        params.put("duration", challenge.getChallengeTemplate().getDuration());
        mailService.sendMail("user", dto.getEmail(), "Challenge invitation", "challenge-invite.html", params, Locale.ENGLISH);
    }

    public UUID authByToken(String token) {
        Invitation invite = invitationRepository.findOne(token);
        if (invite == null) {
            throw new AccessDeniedException("invite.token.invalid");
        }

        if (invite.getExpire().isBefore(ZonedDateTime.now())) {
            throw new AccessDeniedException("invite.token.expired");
        }

        /* create user if not exists */
        User user = userRepository.findByEmail(invite.getEmail());
        if (user == null) {
            UserCreateDto dto = new UserCreateDto();
            dto.setEmail(invite.getEmail());
            dto.setNick(new BigInteger(130, random).toString(32));
            dto.setPassword(new BigInteger(130, random).toString(32));
            userService.createFromDto(dto);

            user = userRepository.findByEmail(invite.getEmail());
        }

        /* invite to challenge if not already invited*/
        Challenge challenge = invite.getChallenge();
        if (!challenge.getInvitedUsers().contains(user)) {
            user.addInvitedChallenge(challenge);
            challengeRepository.save(challenge);
            user = userRepository.save(user);
        }

        /* authenticate */
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return challenge.getId();
    }

    public List<InvitationShowDto> getByChallengeId(UUID challengeId) {
        List<Invitation> invitations = invitationRepository.findAllByChallenge(challengeId);
        List<InvitationShowDto> dtos = new ArrayList<>();
        for (Invitation invitation : invitations) {
            InvitationShowDto dto = new InvitationShowDto();
            dto.setEmail(invitation.getEmail());
            dto.setToken(invitation.getToken());
            dto.setExpire(invitation.getExpire());
            User user = userRepository.findByUsernameOrEmail(invitation.getEmail(), invitation.getEmail());
            if (user != null) {
                dto.setUsername(user.getUsername());
                Result result = resultRepository.findOneByUserAndChallenge(user.getId(), challengeId);
                if (result != null) {
                    dto.setStarted(result.getStarted());
                }
            }
            dtos.add(dto);
        }
        return dtos;
    }

    @Scheduled(fixedRate = 5000)
    public void cleanupTokens() {
        invitationRepository.deleteExpiredTokens(ZonedDateTime.now());
    }
}
