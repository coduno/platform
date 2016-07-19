package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.TeamInvitation;
import uno.cod.platform.server.core.domain.TeamUserKey;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.team.invitation.TeamInvitationShowDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.TeamInvitationRepository;
import uno.cod.platform.server.core.repository.TeamMemberRepository;
import uno.cod.platform.server.core.repository.TeamRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamInvitationService {
    private final TeamInvitationRepository repository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamService teamService;
    private final MailService mailService;

    @Autowired
    public TeamInvitationService(TeamInvitationRepository repository, TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, UserRepository userRepository, TeamService teamService, MailService mailService) {
        this.repository = repository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.teamService = teamService;
        this.mailService = mailService;
    }

    public void create(User invitingUser, User user, Team team, boolean existingCheck) throws MessagingException {
        if (user == null) {
            throw new CodunoIllegalArgumentException("user.invalid");
        }
        if (team == null) {
            throw new CodunoIllegalArgumentException("team.invalid");
        }
        if (team.getInvitedUsers().contains(user)) {
            if (existingCheck) {
                throw new CodunoIllegalArgumentException("team.user.already.invited");
            }
            return;
        }
        TeamUserKey key = new TeamUserKey();
        key.setUser(user);
        key.setTeam(team);
        if (teamMemberRepository.findOne(key) != null) {
            if (existingCheck) {
                throw new CodunoIllegalArgumentException("team.has.member");
            }
            return;
        }
        TeamInvitation invitation = new TeamInvitation();
        invitation.setKey(key);
        invitation.setInvitedBy(invitingUser);
        repository.save(invitation);

        user.addInvitedTeam(team);
        teamRepository.save(team);
        userRepository.save(user);

        Map<String, Object> params = new HashMap<>();
        params.put("teamCanonicalName", team.getCanonicalName());
        params.put("teamName", team.getName());
        params.put("invitedByFull", invitingUser.getName());
        params.put("invitedBy", invitingUser.getUsername());
        params.put("nameFull", user.getName());
        params.put("name", user.getUsername());
        mailService.sendMail(user.getName(), user.getEmail(), "Team invitation", "team-invitation", params, Locale.ENGLISH);
    }

    public void create(User invitingUser, String usernameToInvite, String canonicalName) throws MessagingException {
        User user = userRepository.findByCanonicalName(usernameToInvite);
        Team team = teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName);
        create(invitingUser, user, team, true);
    }

    public void acceptInvitation(User user, String canonicalName) {
        acceptInvitation(user, canonicalName, true);
    }

    public void acceptInvitation(User user, String canonicalName, boolean invitationCheck) {
        Team team = teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName);
        if (team == null) {
            throw new CodunoIllegalArgumentException("team.invalid");
        }

        TeamUserKey key = new TeamUserKey();
        key.setTeam(team);
        key.setUser(user);
        if (invitationCheck) {
            TeamInvitation invitation = repository.findByKey(key);
            if (invitation == null) {
                throw new CodunoIllegalArgumentException("team.invite.notFound");
            }
            repository.delete(invitation);
        }
        teamService.join(user, team);
    }

    public void declineInvitation(User user, String canonicalName) {
        Team team = teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName);
        if (team == null) {
            throw new CodunoIllegalArgumentException("team.invalid");
        }

        TeamUserKey key = new TeamUserKey();
        key.setTeam(team);
        key.setUser(user);
        TeamInvitation invitation = repository.findByKey(key);
        if (invitation == null) {
            throw new CodunoIllegalArgumentException("team.invite.notFound");
        }
        repository.delete(invitation);
    }

    public List<TeamInvitationShowDto> findInvitationsByUserId(UUID userId) {
        return repository.findAllByUserIdAndTeamEnabled(userId).stream().map(TeamInvitationShowDto::new).collect(Collectors.toList());
    }
}
