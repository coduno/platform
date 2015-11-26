package uno.cod.platform.server.core.service;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.domain.OrganizationMember;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.invitation.InvitationDto;
import uno.cod.platform.server.core.dto.user.UserCreateDto;
import uno.cod.platform.server.core.dto.user.UserShowDto;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import javax.persistence.Access;
import java.math.BigInteger;
import java.util.*;

@Service
public class InvitationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final OrganizationRepository organizationRepository;
    private final ChallengeRepository challengeRepository;
    private final Environment environment;
    private final MailService mailService;
    private Logger log = Logger.getLogger(InvitationService.class.getName());
    private Random random = new Random();

    @Autowired
    public InvitationService(UserRepository userRepository, UserService userService, OrganizationRepository organizationRepository, ChallengeRepository challengeRepository, Environment environment, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.organizationRepository = organizationRepository;
        this.challengeRepository = challengeRepository;
        this.environment = environment;
        this.mailService = mailService;
    }

    public void invite(InvitationDto dto, String from) throws MessagingException {
        User invitingUser = userRepository.findByUsername(from);
        Challenge challenge = challengeRepository.findOneWithOrganization(dto.getChallengeId());
        if (challenge == null) {
            throw new IllegalArgumentException("challenge.invalid");
        }
        Organization organization = challenge.getOrganization();
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
        User user = userRepository.findByEmail(dto.getEmail());
        Map<String, Object> params = new HashMap<>();
        String credentials = "";
        if (user == null) {
            UserCreateDto userCreateDto = new UserCreateDto();
            userCreateDto.setEmail(dto.getEmail());
            userCreateDto.setNick(new BigInteger(130, random).toString(32));
            userCreateDto.setPassword(new BigInteger(130, random).toString(32));
            userService.createFromDto(userCreateDto);
            params.put("username", userCreateDto.getNick());
            params.put("password", userCreateDto.getPassword());
            credentials = new String(Base64.getEncoder().encode((userCreateDto.getNick() + ":" + userCreateDto.getPassword()).getBytes()));
        }
        params.put("organization", organization.getName());
        params.put("token", getToken(dto.getChallengeId(), credentials));
        mailService.sendMail("user", dto.getEmail(), "Challenge invitation", "challenge-invite.html", params, Locale.ENGLISH);
    }

    private String getToken(Long challengeId, String credentials) {
        return new String(Base64.getEncoder().encode((challengeId + ":" + credentials).getBytes()));
    }
}
