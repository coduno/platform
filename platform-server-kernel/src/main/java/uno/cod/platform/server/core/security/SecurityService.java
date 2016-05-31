package uno.cod.platform.server.core.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.*;
import uno.cod.platform.server.core.repository.ChallengeRepository;
import uno.cod.platform.server.core.repository.TaskRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import java.util.UUID;

/**
 * even if this does not need to be a spring bean, it is designed as
 * one. it is very likely that we add caching or more stuff here,
 * that needs interaction with other spring compontents, and
 * we do not want to refactor it afterwards
 */
@Service
public class SecurityService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    @Autowired
    public SecurityService(TaskRepository taskRepository, UserRepository userRepository, ChallengeRepository challengeRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
    }

    public boolean isTeamMember(User user, UUID teamId) {
        if (user == null || teamId == null) {
            return false;
        }

        for (TeamMember teamMember : user.getTeams()) {
            if (teamMember.getKey().getTeam().getId().equals(teamId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTeamAdmin(User user, String canonicalName) {
        if (user == null || canonicalName == null) {
            return false;
        }

        user = userRepository.findOneWithTeams(user.getId());
        for (TeamMember teamMember : user.getTeams()) {
            if (teamMember.isAdmin() && teamMember.getKey().getTeam().getCanonicalName().equals(canonicalName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOrganizationMember(User user, UUID organizationId) {
        if (user == null || organizationId == null) {
            return false;
        }

        for (OrganizationMembership organizationMembership : user.getOrganizationMemberships()) {
            if (organizationMembership.getKey().getOrganization().getId().equals(organizationId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOrganizationAdmin(User user, UUID organizationId) {
        if (user == null || organizationId == null) {
            return false;
        }

        for (OrganizationMembership organizationMembership : user.getOrganizationMemberships()) {
            if (organizationMembership.isAdmin() && organizationMembership.getKey().getOrganization().getId().equals(organizationId)) {
                return true;
            }
        }
        return false;
    }

    public boolean canAccessChallenge(String user, String canonicalName) {
        if (canonicalName == null || canonicalName.isEmpty()) {
            return false;
        }
        if (user == null || user.equals("anonymousUser")) {
            Challenge challenge = challengeRepository.findOneByCanonicalName(canonicalName);
            return !challenge.isInviteOnly();
        }
        return canAccessChallenge(userRepository.findByUsername(user), canonicalName);
    }

    public boolean canAccessChallenge(User user, String canonicalName) {
        if (canonicalName == null || canonicalName.isEmpty()) {
            return false;
        }
        if (user == null) {
            Challenge challenge = challengeRepository.findOneByCanonicalName(canonicalName);
            return !challenge.isInviteOnly();
        }
        user = userRepository.findOne(user.getId());

        // TODO add organization check
        if (user.getOrganizationMemberships() != null) {
            return true;
        }
        // TODO: remove, an invite must be changed to a participation in order to participate
        for (Challenge challenge : user.getInvitedChallenges()) {
            if (challenge.getCanonicalName().equals(canonicalName)) {
                return true;
            }
        }
        // Is registered to challenge
        for (Participation participation : user.getParticipations()) {
            if (participation.getKey().getChallenge().getCanonicalName().equals(canonicalName)) {
                return true;
            }
        }
        return false;
    }

    public boolean canAccessChallengeTemplate(User user, UUID templateId) {
        if (user == null || templateId == null) {
            return false;
        }
        user = userRepository.findOne(user.getId());

        // TODO add organization check
        if (user.getOrganizationMemberships() != null) {
            return true;
        }
        return false;
    }

    public boolean canAccessTask(User user, UUID taskId) {
        if (user == null || taskId == null) {
            return false;
        }

        // TODO extend for current organization the user is logged in
        if (user.getOrganizationMemberships() != null) {
            Task task = taskRepository.findOneWithOrganization(taskId);
            if (task.isPublic()) {
                return true;
            }
            for (OrganizationMembership membership : user.getOrganizationMemberships()) {
                if (membership.getKey().getOrganization().getId().equals(task.getOrganization().getId())) {
                    return true;
                }
            }
        }

        user = userRepository.findOneWithResults(user.getId());
        for (Result result : user.getResults()) {
            if (result.getFinished() == null) {
                for (TaskResult taskResult : result.getTaskResults()) {
                    if (taskResult.getKey().getTask().getId().equals(taskId) && taskResult.getStartTime() != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
