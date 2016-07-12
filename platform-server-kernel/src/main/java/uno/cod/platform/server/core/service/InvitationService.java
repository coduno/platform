package uno.cod.platform.server.core.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.invitation.InvitationDto;
import uno.cod.platform.server.core.dto.invitation.InvitationShowDto;
import uno.cod.platform.server.core.exception.CodunoAccessDeniedException;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;
import uno.cod.platform.server.core.util.UsernameUtil;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class InvitationService {
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;
    private final ChallengeRepository challengeRepository;
    private final MailService mailService;
    private final UserService userService;
    private final GithubService githubService;
    private final HttpSession httpSession;
    private final ActivationTokenService authenticationTokenService;
    private final Random random;

    @Value("#{T(java.time.Duration).parse('${coduno.invite.expire}')}")
    private Duration duration;

    private Logger log = Logger.getLogger(InvitationService.class.getName());

    @Autowired
    public InvitationService(UserRepository userRepository,
                             ResultRepository resultRepository, ChallengeRepository challengeRepository,
                             UserService userService,
                             MailService mailService,
                             GithubService githubService,
                             HttpSession httpSession,
                             ActivationTokenService authenticationTokenService) {
        this.userRepository = userRepository;
        this.resultRepository = resultRepository;
        this.challengeRepository = challengeRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.githubService = githubService;
        this.httpSession = httpSession;
        this.authenticationTokenService = authenticationTokenService;
        this.random = new Random();
    }

    public void create(String email, String challengeCanonicalName) {
        Challenge challenge = challengeRepository.findOneByCanonicalName(challengeCanonicalName);
        List<String> guessedUsernames = githubService.guessUsername(email);
        String guessedUsername = guessedUsernames.isEmpty() ? UsernameUtil.randomUsername() : guessedUsernames.get(0);
        String randomPassword = new BigInteger(130, random).toString(32);
        authenticationTokenService.createToken(email, guessedUsername, randomPassword, challenge, ZonedDateTime.now().plus(duration));

        // TODO: send email with token
    }

    public void invite(InvitationDto dto, String from) throws MessagingException {
        User invitingUser = userRepository.findByUsername(from);
        Challenge challenge = challengeRepository.findOneByCanonicalName(dto.getCanonicalName());

        Organization organization = challenge.getChallengeTemplate().getOrganization();
        boolean ok = false;
        for (OrganizationMembership organizationMembership : invitingUser.getOrganizationMemberships()) {
            if (organizationMembership.isAdmin() && organizationMembership.getKey().getOrganization().getId().equals(organization.getId())) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new CodunoAccessDeniedException("challenge.access.denied");
        }

        User user = userRepository.findByEmail(dto.getEmail());
        if (user != null) {
            for (Participation participation: user.getParticipations()) {
                if (participation.getKey().getChallenge().getId().equals(challenge.getId())) {
                    throw new CodunoIllegalArgumentException("user.already.registered.to.challenge");
                }
            }

            user.addInvitedChallenge(challenge);
            challengeRepository.save(challenge);
            userRepository.save(user);
        }

        String token = new BigInteger(130, random).toString(32);

        /*Invitation invitation = new Invitation();
        invitation.setChallenge(challenge);
        invitation.setEmail(dto.getEmail());
        if (challenge.getStartDate() != null) {
            invitation.setExpire(challenge.getStartDate().plus(challenge.getChallengeTemplate().getDuration()));
        } else {
            invitation.setExpire(ZonedDateTime.now().plus(duration));
        }

        UUID uuid = invitationRepository.save(invitation).getId();

        Map<String, Object> params = new HashMap<>();
        params.put("organization", organization.getName());
        params.put("token", uuid.toString());
        params.put("startDate", challenge.getStartDate());
        params.put("duration", challenge.getChallengeTemplate().getDuration());
        mailService.sendMail("user", dto.getEmail(), "Challenge invitation", "challenge-invite", params, Locale.ENGLISH);
    */
    }

    public String authByToken(String token) {
       /* Invitation invite = invitationRepository.findOne(token);
        if (invite == null) {
            throw new CodunoAccessDeniedException("invite.token.invalid");
        }

        if (invite.getExpire().isBefore(ZonedDateTime.now())) {
            throw new CodunoAccessDeniedException("invite.token.expired");
        }

        /* create user if not exists */
        /*User user = userRepository.findByEmail(invite.getEmail());
        Challenge challenge = invite.getChallenge();
        if (user == null) {
            UserCreateDto dto = new UserCreateDto();
            dto.setEmail(invite.getEmail());
            List<String> guessedUsernames = githubService.guessUsername(invite.getEmail());
            dto.setNick(guessedUsernames.isEmpty() ? UsernameUtil.randomUsername() : guessedUsernames.get(0));
            dto.setPassword(new BigInteger(130, random).toString(32));
            user = userService.createFromDto(dto);

            /* invite to challenge if not already invited*/
            /*if (!challenge.getInvitedUsers().contains(user)) {
                user.addInvitedChallenge(challenge);
                challengeRepository.save(challenge);
                user = userRepository.save(user);
            }
        }

        /* authenticate */
        /*UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return challenge.getCanonicalName();*/

        return "";
    }

    public List<InvitationShowDto> getByChallengeId(UUID challengeId) {
        /*List<Invitation> invitations = invitationRepository.findAllByChallenge(challengeId);
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
        return dtos;*/

        return null;
    }
    public Set<InvitationShowDto> getByChallengeCanonicalName(String canonicalName) {
        /*Set<Invitation> invitations = invitationRepository.findAllByChallengeCanonicalName(canonicalName);
        Set<InvitationShowDto> dtos = new HashSet<>();
        for (Invitation invitation : invitations) {
            InvitationShowDto dto = new InvitationShowDto();
            dto.setEmail(invitation.getEmail());
            dto.setToken(invitation.getToken());
            dto.setExpire(invitation.getExpire());
            User user = userRepository.findByUsernameOrEmail(invitation.getEmail(), invitation.getEmail());
            if (user != null) {
                dto.setUsername(user.getUsername());
            }
            // TODO - Will show started and other info in get results for challenge
            dtos.add(dto);
        }
        return dtos;*/

        return null;
    }

    @Scheduled(fixedRate = 5000)
    public void cleanupTokens() {
        /*invitationRepository.deleteExpiredTokens(ZonedDateTime.now());*/
    }
}
