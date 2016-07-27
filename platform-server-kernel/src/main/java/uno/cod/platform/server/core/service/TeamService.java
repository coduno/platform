package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.TeamInvitation;
import uno.cod.platform.server.core.domain.TeamMember;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.team.TeamCreateDto;
import uno.cod.platform.server.core.dto.team.TeamShowDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.TeamInvitationRepository;
import uno.cod.platform.server.core.repository.TeamMemberRepository;
import uno.cod.platform.server.core.repository.TeamRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInvitationRepository teamInvitationRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       TeamInvitationRepository teamInvitationRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamInvitationRepository = teamInvitationRepository;
    }

    public void create(TeamCreateDto dto, User user) {
        if (teamRepository.findByCanonicalName(dto.getCanonicalName()) != null) {
            throw new CodunoIllegalArgumentException("team.canonicalName.existing");
        }

        Team team = new Team(dto.getCanonicalName(), dto.getName());
        team = teamRepository.save(team);

        TeamMember member = new TeamMember(team, user, true);
        teamMemberRepository.save(member);
    }

    void join(User user, Team team) {
        TeamMember member = new TeamMember(team, user);
        teamMemberRepository.save(member);
    }

    public void delete(String canonicalName) {
        Team team = teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName);
        List<TeamInvitation> invites = teamInvitationRepository.findAllByTeam(team);
        invites.forEach(teamInvitationRepository::delete);
        team.setEnabled(false);
        teamRepository.save(team);
    }

    public TeamShowDto findOne(String canonicalName) {
        return new TeamShowDto(teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName));
    }

    public List<TeamShowDto> findAllTeamsForUser(String username) {
        return teamRepository.findAllByUserCanonicalName(username).stream().map(TeamShowDto::new).collect(Collectors.toList());
    }
}
