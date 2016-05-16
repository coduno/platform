package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.team.TeamCreateDto;
import uno.cod.platform.server.core.dto.team.TeamShowDto;
import uno.cod.platform.server.core.repository.CanonicalNameRepository;
import uno.cod.platform.server.core.repository.TeamInvitationRepository;
import uno.cod.platform.server.core.repository.TeamMemberRepository;
import uno.cod.platform.server.core.repository.TeamRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {
    private final TeamRepository repository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInvitationRepository teamInvitationRepository;
    private final SessionService sessionService;
    private final CanonicalNameRepository canonicalNameRepository;

    @Autowired
    public TeamService(TeamRepository repository, TeamMemberRepository teamMemberRepository, TeamInvitationRepository teamInvitationRepository, SessionService sessionService, CanonicalNameRepository canonicalNameRepository) {
        this.repository = repository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamInvitationRepository = teamInvitationRepository;
        this.sessionService = sessionService;
        this.canonicalNameRepository = canonicalNameRepository;
    }

    public void create(TeamCreateDto dto) {
        if (canonicalNameRepository.findOne(dto.getCanonicalName()) != null) {
            throw new IllegalArgumentException("team.canonicalName.existing");
        }
        Team team = new Team();
        team.setName(dto.getName());
        CanonicalName canonicalName = new CanonicalName();
        canonicalName.setValue(dto.getCanonicalName());
        team.setCanonicalName(canonicalName);
        team = repository.save(team);

        User user = sessionService.getLoggedInUser();

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
        Team team = repository.findByCanonicalNameValueAndEnabledTrue(canonicalName);
        team.setEnabled(false);
        repository.save(team);

        teamInvitationRepository.deleteAllForTeam(team);
    }

    public TeamShowDto findOne(String canonicalName) {
        return new TeamShowDto(repository.findByCanonicalNameValueAndEnabledTrue(canonicalName));
    }

    public List<TeamShowDto> findAllTeamsForUser(String username) {
        return repository.findAllByUsername(username).stream().map(TeamShowDto::new).collect(Collectors.toList());
    }
}
