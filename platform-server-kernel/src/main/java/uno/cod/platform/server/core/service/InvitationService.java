package uno.cod.platform.server.core.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.invitation.InvitationDto;
import uno.cod.platform.server.core.dto.invitation.InvitationShowDto;
import uno.cod.platform.server.core.exception.CodunoAccessDeniedException;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.ActivationTokenRepository;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ResultRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;
import uno.cod.platform.server.core.util.TokenHelper;
import uno.cod.platform.server.core.util.UsernameUtil;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@Transactional
public class InvitationService {
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;
    private final ChallengeRepository challengeRepository;
    private final MailService mailService;
    private final GithubService githubService;
    private final HttpSession httpSession;
    private final ActivationTokenRepository activationTokenRepository;
    private final ActivationTokenService activationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final Random random;

    @Value("#{T(java.time.Duration).parse('${coduno.invite.expire}')}")
    private Duration duration;

    private Logger log = Logger.getLogger(InvitationService.class.getName());

    @Autowired
    public InvitationService(UserRepository userRepository,
                             ResultRepository resultRepository, ChallengeRepository challengeRepository,
                             MailService mailService,
                             GithubService githubService,
                             HttpSession httpSession,
                             ActivationTokenRepository activationTokenRepository,
                             ActivationTokenService activationTokenService,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resultRepository = resultRepository;
        this.challengeRepository = challengeRepository;
        this.mailService = mailService;
        this.githubService = githubService;
        this.httpSession = httpSession;
        this.activationTokenRepository = activationTokenRepository;
        this.activationTokenService = activationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.random = new Random();
    }

    public void invite(InvitationDto dto, String from) throws MessagingException {
        User invitingUser = userRepository.findByCanonicalName(from);
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

        ActivationToken invitation = new ActivationToken();
        invitation.setChallenge(challenge);
        invitation.setEmail(dto.getEmail());
        List<String> guessedUsernames = githubService.guessUsername(dto.getEmail());
        invitation.setUsername(guessedUsernames.isEmpty() ? UsernameUtil.randomUsername() : guessedUsernames.get(0));
        invitation.setPassword(passwordEncoder.encode(new BigInteger(130, random).toString(32)));
        if (challenge.getStartDate() != null) {
            invitation.setExpire(challenge.getStartDate().plus(challenge.getChallengeTemplate().getDuration()));
        } else {
            invitation.setExpire(ZonedDateTime.now().plus(duration));
        }

        invitation.setToken(passwordEncoder.encode(token));
        invitation = activationTokenRepository.save(invitation);

        byte[] bytes = (invitation.getId() + ":" + token).getBytes();
        String emailToken = new String(org.springframework.security.crypto.codec.Base64.encode(bytes));

        Map<String, Object> params = new HashMap<>();
        params.put("organization", organization.getName());
        params.put("token", emailToken);
        params.put("startDate", challenge.getStartDate());
        params.put("duration", challenge.getChallengeTemplate().getDuration());
        mailService.sendMail("user", dto.getEmail(), "Challenge invitation", "challenge-invite", params, Locale.ENGLISH);
    }

    public String authByToken(String token) {
        String[] authToken = TokenHelper.decodeAndSplit(token);
        String checkToken = authToken[1];
        ActivationToken invite = activationTokenService.findTokenById(UUID.fromString(authToken[0]));

        if (invite == null || !passwordEncoder.matches(checkToken, invite.getToken())) {
            throw new CodunoAccessDeniedException("invite.token.invalid");
        }

        if (invite.getExpire().isBefore(ZonedDateTime.now())) {
            throw new CodunoAccessDeniedException("invite.token.expired");
        }

        /* create user if not exists */
        User user = userRepository.findByEmail(invite.getEmail());
        Challenge challenge = invite.getChallenge();
        if (user == null) {
            user = new User();
            user.setEmail(invite.getEmail());
            user.setUsername(UsernameUtil.randomUsername());
            user.setPassword(new BigInteger(130, random).toString(32));
            user = userRepository.save(user);

            /* invite to challenge if not already invited*/
            if (!challenge.getInvitedUsers().contains(user)) {
                user.addInvitedChallenge(challenge);
                challengeRepository.save(challenge);
                user = userRepository.save(user);
            }
        }

        /* authenticate */
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return challenge.getCanonicalName();
    }

    public List<InvitationShowDto> getByChallengeId(UUID challengeId) {
        List<ActivationToken> invitations = activationTokenRepository.findAllByChallenge(challengeId);
        List<InvitationShowDto> dtos = new ArrayList<>();
        for (ActivationToken invitation : invitations) {
            InvitationShowDto dto = new InvitationShowDto();
            dto.setEmail(invitation.getEmail());
            dto.setToken(invitation.getToken());
            dto.setExpire(invitation.getExpire());
            User user = userRepository.findByCanonicalNameOrEmail(invitation.getEmail(), invitation.getEmail());
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
    public Set<InvitationShowDto> getByChallengeCanonicalName(String canonicalName) {
        Set<ActivationToken> invitations = activationTokenRepository.findAllByChallengeCanonicalName(canonicalName);
        Set<InvitationShowDto> dtos = new HashSet<>();
        for (ActivationToken invitation : invitations) {
            InvitationShowDto dto = new InvitationShowDto();
            dto.setEmail(invitation.getEmail());
            dto.setToken(invitation.getToken());
            dto.setExpire(invitation.getExpire());
            User user = userRepository.findByCanonicalNameOrEmail(invitation.getEmail(), invitation.getEmail());
            if (user != null) {
                dto.setUsername(user.getUsername());
            }
            // TODO - Will show started and other info in get results for challenge
            dtos.add(dto);
        }
        return dtos;
    }

    @Scheduled(fixedRate = 5000)
    public void cleanupTokens() {
        activationTokenRepository.deleteExpiredTokens(ZonedDateTime.now());
    }
}
