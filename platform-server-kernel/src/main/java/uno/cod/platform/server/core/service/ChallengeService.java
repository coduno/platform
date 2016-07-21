package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.dto.challenge.ChallengeCreateDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeDto;
import uno.cod.platform.server.core.dto.challenge.ChallengeUpdateDto;
import uno.cod.platform.server.core.dto.challenge.UserChallengeShowDto;
import uno.cod.platform.server.core.dto.location.LocationDetailShowDto;
import uno.cod.platform.server.core.dto.location.LocationDetailUpdateDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.exception.CodunoResourceConflictException;
import uno.cod.platform.server.core.mapper.ChallengeMapper;
import uno.cod.platform.server.core.repository.*;

import javax.validation.constraints.NotNull;
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
    private final LocationDetailRepository locationDetailRepository;

    @Autowired
    public ChallengeService(ChallengeRepository repository,
                            ChallengeTemplateRepository challengeTemplateRepository,
                            ResultRepository resultRepository,
                            LocationRepository locationRepository,
                            LocationDetailRepository locationDetailRepository) {
        this.repository = repository;
        this.challengeTemplateRepository = challengeTemplateRepository;
        this.resultRepository = resultRepository;
        this.locationRepository = locationRepository;
        this.locationDetailRepository = locationDetailRepository;
    }

    public String updateChallengeInfo(ChallengeUpdateDto dto) {
        Challenge challenge = repository.findOne(dto.getId());
        if (challenge == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }

        Challenge duplicate = repository.findOneByCanonicalName(dto.getCanonicalName());
        if (duplicate != null && !duplicate.getId().equals(challenge.getId())) {
            throw new CodunoResourceConflictException("challenge.canonicalName.existing", new String[]{dto.getCanonicalName()});
        }

        challenge.setName(dto.getName());
        challenge.setCanonicalName(dto.getCanonicalName());
        challenge.setStartDate(dto.getStartDate());
        if (dto.getStartDate() != null) {
            challenge.setEndDate(dto.getStartDate().plus(challenge.getChallengeTemplate().getDuration()));
        } else {
            challenge.setEndDate(null);
        }
        challenge.setInviteOnly(dto.isInviteOnly());
        return repository.save(challenge).getCanonicalName();
    }

    public String createFromDto(ChallengeCreateDto dto) {
        ChallengeTemplate template = challengeTemplateRepository.findOneByCanonicalName(dto.getTemplateCanonicalName());
        if (template == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }

        if (repository.findOneByCanonicalName(dto.getCanonicalName()) != null) {
            throw new CodunoResourceConflictException("challenge.canonicalName.existing", new String[]{dto.getCanonicalName()});
        }

        Challenge challenge = new Challenge(dto.getCanonicalName(), dto.getName());
        challenge.setChallengeTemplate(template);
        if (dto.getStartDate() != null) {
            challenge.setStartDate(dto.getStartDate());
            challenge.setEndDate(dto.getStartDate().plus(template.getDuration()));
        }
        if (dto.getLocations() != null) {
            for (LocationDetailUpdateDto locationDto : dto.getLocations()) {
                locationDetailRepository.save(createLocationDetailFromDto(locationDto, challenge));
            }
        }
        challenge.setInviteOnly(dto.isInviteOnly());
        return repository.save(challenge).getCanonicalName();
    }

    public void updateLocations(String canonicalName, List<LocationDetailUpdateDto> locations) {
        Challenge challenge = repository.findOneByCanonicalName(canonicalName);

        if (challenge == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }

        for (LocationDetail detail : challenge.getLocationDetails()) {
            boolean found = false;
            for (LocationDetailUpdateDto dto : locations) {
                if (detail.getKey().getLocation().getId().equals(dto.getId())) {
                    found = true;
                }
            }
            if (!found) {
                locationDetailRepository.delete(new ChallengeLocationKey(challenge, detail.getKey().getLocation()));
            }
        }

        for (LocationDetailUpdateDto dto : locations) {
            locationDetailRepository.save(createLocationDetailFromDto(dto, challenge));
        }
    }

    private LocationDetail createLocationDetailFromDto(LocationDetailUpdateDto dto, Challenge challenge) {
        Location location = locationRepository.findOne(dto.getId());

        if (location == null) {
            location = new Location();
            location.setId(dto.getId());
            location.setLatitude(dto.getLatitude());
            location.setLongitude(dto.getLongitude());
            location = locationRepository.save(location);
        }

        LocationDetail locationDetail = new LocationDetail();
        locationDetail.setName(dto.getName());
        locationDetail.setDescription(dto.getDescription());
        locationDetail.setAddress(dto.getAddress());
        locationDetail.setKey(new ChallengeLocationKey(challenge, location));

        return locationDetailRepository.save(locationDetail);
    }

    @NotNull
    public ChallengeDto findOneByCanonicalName(String canonicalName) {
        final ChallengeDto result = ChallengeMapper.map(repository.findOneByCanonicalName(canonicalName));
        if (result == null) {
            throw new CodunoIllegalArgumentException("challenge.invalid");
        }
        return result;
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
        List<Challenge> publicChallenges = repository.findAllPublicChallenges();
        return mapChallengesToUserChallengeShowDto(publicChallenges, user)
                .stream()
                .sorted((a, b) -> Integer.compare(a.getStatus().getValue(), b.getStatus().getValue()))
                .collect(Collectors.toList());
    }

    public List<UserChallengeShowDto> getInviteOnlyChallenges(final User user) {
        List<Challenge> invitedChallenges = repository.findAllByInvitedUser(user.getId());
        return mapChallengesToUserChallengeShowDto(invitedChallenges, user);
    }

    public List<UserChallengeShowDto> mapChallengesToUserChallengeShowDto(List<Challenge> list, User user) {
        return list.stream().map(challenge -> {
            UserChallengeShowDto dto = new UserChallengeShowDto();
            dto.setChallenge(new ChallengeDto(challenge));
            addStatusAndLocation(dto, challenge, user);
            return dto;
        }).filter(p -> p != null).collect(Collectors.toList());
    }

    private void addStatusAndLocation(UserChallengeShowDto dto, Challenge challenge, User user) {
        UserChallengeShowDto.ChallengeStatus status = UserChallengeShowDto.ChallengeStatus.OPEN;

        if (challenge.isInviteOnly()) {
            status = UserChallengeShowDto.ChallengeStatus.INVITE_ONLY;
        }

        if (challenge.getEndDate() != null && challenge.getEndDate().isAfter(ZonedDateTime.now())) {
            status = UserChallengeShowDto.ChallengeStatus.ENDED;
        }

        if (challenge.getInvitedUsers() != null && challenge.getInvitedUsers().contains(user)) {
            status = UserChallengeShowDto.ChallengeStatus.INVITED;
        }

        if (challenge.getParticipations() != null) {
            for (Participation participation : challenge.getParticipations()) {
                if (participation.getKey().getUser().equals(user)) {
                    status = UserChallengeShowDto.ChallengeStatus.REGISTERED;

                    addLocation(dto, participation);

                    if (participation.getTeam() != null) {
                        dto.setRegisteredAs(participation.getTeam().getName());
                    } else {
                        dto.setRegisteredAs(user.getUsername());
                    }
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

        if (challenge.getEndDate() != null &&
                challenge.getEndDate().isBefore(ZonedDateTime.now()) &&
                !status.equals(UserChallengeShowDto.ChallengeStatus.COMPLETED)) {
            status = UserChallengeShowDto.ChallengeStatus.ENDED;
        }

        dto.setStatus(status);
    }

    private void addLocation(UserChallengeShowDto dto, Participation participation) {
        if (participation.getLocation() != null) {
            for (LocationDetail locationDetail : participation.getKey().getChallenge().getLocationDetails()) {
                if (!locationDetail.getKey().getLocation().getId().equals(participation.getLocation().getId())) {
                    continue;
                }
                dto.setLocation(new LocationDetailShowDto(locationDetail));
                break;
            }
        }
    }
}
