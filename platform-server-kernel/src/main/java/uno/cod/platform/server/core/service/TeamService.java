package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.*;
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
        if (teamRepository.findByNameAndEnabledTrue(dto.getName()) != null) {
            throw new CodunoIllegalArgumentException("team.name.existing");
        }

        Team team = new Team();
        team.setName(dto.getName());
        team.setCanonicalName(dto.getCanonicalName());
        team = teamRepository.save(team);

        TeamUserKey key = new TeamUserKey();
        key.setTeam(team);
        key.setUser(user);
        TeamMember member = new TeamMember();
        member.setKey(key);
        member.setAdmin(true);
        teamMemberRepository.save(member);
    }

    void join(User user, Team team) {
        TeamUserKey key = new TeamUserKey();
        key.setTeam(team);
        key.setUser(user);
        TeamMember member = new TeamMember();
        member.setKey(key);
        teamMemberRepository.save(member);
    }

    public void delete(String canonicalName) {
        Team team = teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName);
        List<TeamInvitation> invites = teamInvitationRepository.findAllByTeam(team);
        for (TeamInvitation i : invites) {
            teamInvitationRepository.delete(i);
        }
        team.setEnabled(false);
        teamRepository.save(team);

    }

    public TeamShowDto findOne(String canonicalName) {
        return new TeamShowDto(teamRepository.findByCanonicalNameAndEnabledTrue(canonicalName));
    }

    public List<TeamShowDto> findAllTeamsForUser(String username) {
        return teamRepository.findAllByUsername(username).stream().map(TeamShowDto::new).collect(Collectors.toList());
    }
}
