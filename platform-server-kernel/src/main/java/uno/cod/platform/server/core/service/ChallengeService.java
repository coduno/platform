package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.dto.challenge.UserChallengeShowDto;
import uno.cod.platform.server.core.dto.location.LocationCreateDto;
import uno.cod.platform.server.core.dto.location.LocationShowDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.exception.CodunoResourceConflictException;
import uno.cod.platform.server.core.mapper.ChallengeMapper;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.ChallengeTemplateRepository;
import uno.cod.platform.server.core.repository.LocationRepository;
import uno.cod.platform.server.core.repository.ResultRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChallengeService {
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final ChallengeRepository repository;
    private final ResultRepository resultRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public ChallengeService(ChallengeRepository repository,
                            ChallengeTemplateRepository challengeTemplateRepository,
                            ResultRepository resultRepository,
                            LocationRepository locationRepository) {
        this.repository = repository;
        this.challengeTemplateRepository = challengeTemplateRepository;
        this.resultRepository = resultRepository;
        this.locationRepository = locationRepository;
    }

    public String createFromDto(ChallengeCreateDto dto) {
        ChallengeTemplate template = challengeTemplateRepository.findOne(dto.getTemplateId());
        if (template == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }

        if (repository.findOneByCanonicalName(dto.getCanonicalName()) != null) {
            throw new CodunoResourceConflictException("challenge.canonicalName.existing", new String[]{dto.getCanonicalName()});
        }
        Challenge challenge = new Challenge();
        challenge.setChallengeTemplate(template);
        challenge.setName(dto.getName());
        challenge.setCanonicalName(dto.getCanonicalName());
        if (dto.getStartDate() != null) {
            challenge.setStartDate(dto.getStartDate());
            challenge.setEndDate(dto.getStartDate().plus(template.getDuration()));
        }
        if (dto.getLocations() != null) {
            for (LocationCreateDto locationDto : dto.getLocations()) {
                challenge.addLocation(createLocationFromDto(locationDto));
            }
        }
        challenge.setInviteOnly(dto.isInviteOnly());
        return repository.save(challenge).getCanonicalName();
    }

    private Location createLocationFromDto(LocationCreateDto dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setDescription(dto.getDescription());
        location.setAddress(dto.getAddress());
        location.setPlaceId(dto.getPlaceId());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        return locationRepository.save(location);
    }

    public ChallengeDto findOneByCanonicalName(String canonicalName) {
        return ChallengeMapper.map(repository.findOneByCanonicalName(canonicalName));
    }

    public ChallengeDto findOneById(UUID challengeId) {
        return ChallengeMapper.map(repository.findOne(challengeId));
    }

    public UserChallengeShowDto getChallengeStatusForUser(String name, User user) {
        UserChallengeShowDto dto = new UserChallengeShowDto();
        Challenge challenge = repository.findOneByCanonicalNameWithInvitedUsersAndRegisteredUsers(name);
        addStatusAndLocation(dto, challenge, user);
        return dto;
    }

    public List<UserChallengeShowDto> getPublicChallenges(final User user) {
        List<Challenge> challenges = repository.findAllValidWithOrganizationAndInvitedUsersAndRegisteredUsers(user.getId());
        return challenges.stream().map(challenge -> {
            UserChallengeShowDto dto = new UserChallengeShowDto();
            dto.setChallenge(new ChallengeDto(challenge));
            addStatusAndLocation(dto, challenge, user);

            return dto;

        }).filter(p -> p != null).collect(Collectors.toList());
    }

    private void addStatusAndLocation(UserChallengeShowDto dto, Challenge challenge, User user) {
        UserChallengeShowDto.ChallengeStatus status = null;

        if (challenge.getInvitedUsers() != null && challenge.getInvitedUsers().contains(user)) {
            status = UserChallengeShowDto.ChallengeStatus.INVITED;
        } else if (challenge.getParticipations() != null) {
            for (Participation participation : challenge.getParticipations()) {
                if (participation.getKey().getUser().equals(user)) {
                    status = UserChallengeShowDto.ChallengeStatus.REGISTERED;
                    if (participation.getLocation() != null) {
                        dto.setLocation(new LocationShowDto(participation.getLocation()));
                    }
                    if (participation.getTeam() != null) {
                        dto.setRegisteredAs(participation.getTeam().getName());
                    } else {
                        dto.setRegisteredAs(user.getUsername());
                    }
                    break;
                }
            }
        }
        Result result = resultRepository.findOneByUserAndChallenge(user.getId(), challenge.getId());
        if (result != null && result.getStarted() != null) {
            if (result.getFinished() != null) {
                status = UserChallengeShowDto.ChallengeStatus.COMPLETED;
            } else {
                status = UserChallengeShowDto.ChallengeStatus.IN_PROGRESS;
            }
        }
        if (status == null & challenge.isInviteOnly()) {
            status = UserChallengeShowDto.ChallengeStatus.INVITE_ONLY;
        }

        if (status == null) {
            status = UserChallengeShowDto.ChallengeStatus.OPEN;
        }
        if (challenge.getEndDate() != null &&
                challenge.getEndDate().isBefore(ZonedDateTime.now()) &&
                !status.equals(UserChallengeShowDto.ChallengeStatus.COMPLETED)) {
            status = UserChallengeShowDto.ChallengeStatus.ENDED;
        }
        dto.setStatus(status);
    }

}
