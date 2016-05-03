package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.TeamInvitation;
import uno.cod.platform.server.core.domain.TeamUserKey;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.team.invitation.TeamInvitationShowDto;
import uno.cod.platform.server.core.repository.TeamInvitationRepository;
import uno.cod.platform.server.core.repository.TeamMemberRepository;
import uno.cod.platform.server.core.repository.TeamRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamInvitationService {
    private final TeamInvitationRepository repository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamService teamService;

    @Autowired
    public TeamInvitationService(TeamInvitationRepository repository, TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, UserRepository userRepository, TeamService teamService) {
        this.repository = repository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.teamService = teamService;
    }

    public void create(User invitingUser, String usernameToInvite, String canonicalName) {
        User user = userRepository.findByUsername(usernameToInvite);
        if (user == null) {
            throw new IllegalArgumentException("user.invalid");
        }
        Team team = teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName);
        if (team == null) {
            throw new IllegalArgumentException("team.invalid");
        }
        if (team.getInvitedUsers().contains(user)) {
            throw new IllegalArgumentException("team.user.already.invited");
        }
        TeamUserKey key = new TeamUserKey();
        key.setUser(user);
        key.setTeam(team);
        if (teamMemberRepository.findOne(key) != null) {
            throw new IllegalArgumentException("team.has.member");
        }
        TeamInvitation invitation = new TeamInvitation();
        invitation.setKey(key);
        invitation.setInvitedBy(invitingUser);
        repository.save(invitation);

        user.addInvitedTeam(team);
        teamRepository.save(team);
        userRepository.save(user);
    }

    public void acceptInvitation(User user, String canonicalName) {
        Team team = teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName);
        if (team == null) {
            throw new IllegalArgumentException("team.invalid");
        }

        TeamUserKey key = new TeamUserKey();
        key.setTeam(team);
        key.setUser(user);
        TeamInvitation invitation = repository.findByKey(key);
        if (invitation == null) {
            throw new IllegalArgumentException("team.invite.notfound");
        }
        teamService.join(user, team);
        repository.delete(invitation);
    }

    public void declineInvitation(User user, String canonicalName) {
        Team team = teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName);
        if (team == null) {
            throw new IllegalArgumentException("team.invalid");
        }

        TeamUserKey key = new TeamUserKey();
        key.setTeam(team);
        key.setUser(user);
        TeamInvitation invitation = repository.findByKey(key);
        if (invitation == null) {
            throw new IllegalArgumentException("team.invite.notfound");
        }
        repository.delete(invitation);
    }

    public List<TeamInvitationShowDto> findInvitationsByUserId(UUID userId) {
        return repository.findAllByUserIdAndTeamEnabled(userId).stream().map(TeamInvitationShowDto::new).collect(Collectors.toList());
    }
}
