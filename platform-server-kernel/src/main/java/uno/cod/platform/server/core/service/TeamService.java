package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Team;
import uno.cod.platform.server.core.domain.TeamMember;
import uno.cod.platform.server.core.domain.TeamUserKey;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.team.TeamCreateDto;
import uno.cod.platform.server.core.dto.team.TeamShowDto;
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
    private final SessionService sessionService;

    @Autowired
    public TeamService(TeamRepository repository, TeamMemberRepository teamMemberRepository, SessionService sessionService) {
        this.repository = repository;
        this.teamMemberRepository = teamMemberRepository;
        this.sessionService = sessionService;
    }

    public void create(TeamCreateDto dto) {
        if (repository.findOneByCanonicalName(dto.getCanonicalName()) != null) {
            throw new IllegalArgumentException("team.canonicalName.existing");
        }
        Team team = new Team();
        team.setName(dto.getName());
        team.setCanonicalName(dto.getCanonicalName());
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

    public void join(User user, String canonicalName) {
        Team team = repository.findOneByCanonicalName(canonicalName);
        if (team == null) {
            throw new IllegalArgumentException("team.invalid");
        }
        TeamUserKey key = new TeamUserKey();
        key.setTeam(team);
        key.setUser(user);
        TeamMember member = new TeamMember();
        member.setKey(key);
        teamMemberRepository.save(member);
        //TODO maybe delete invitation after join/decline?
    }

    public void delete(String canonicalName) {
        Team team = repository.findOneByCanonicalName(canonicalName);
        team.setEnabled(false);
        repository.save(team);
    }

    public TeamShowDto findOne(String canonicalName) {
        return new TeamShowDto(repository.findOneByCanonicalName(canonicalName));
    }

    public List<TeamShowDto> findAllTeamsForUser(String username) {
        return repository.findAllByUsername(username).stream().map(TeamShowDto::new).collect(Collectors.toList());
    }
}
