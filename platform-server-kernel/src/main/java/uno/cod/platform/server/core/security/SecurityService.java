package uno.cod.platform.server.core.security;


import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Challenge;
import uno.cod.platform.server.core.domain.OrganizationMember;
import uno.cod.platform.server.core.domain.TeamMember;
import uno.cod.platform.server.core.domain.User;

import java.util.Set;

/**
 * even if this does not need to be a spring bean, it is designed as
 * one. it is very likely that we add caching or more stuff here,
 * that needs interaction with other spring compontents, and
 * we do not want to refactor it afterwards
 */
@Service
public class SecurityService {
    public boolean isTeamMember(User user, Long teamId) {
        Set<TeamMember> teams = user.getTeams();
        for (TeamMember teamMember : user.getTeams()) {
            if (teamMember.getKey().getTeam().getId().equals(teamId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTeamAdmin(User user, Long teamId) {
        Set<TeamMember> teams = user.getTeams();
        for (TeamMember teamMember : user.getTeams()) {
            if (teamMember.isAdmin() && teamMember.getKey().getTeam().getId().equals(teamId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOrganizationMember(User user, Long organizationId) {
        if(organizationId == null){
            return false;
        }
        Set<OrganizationMember> organizations = user.getOrganizations();
        for (OrganizationMember organizationMember : user.getOrganizations()) {
            if (organizationMember.getKey().getOrganization().getId().equals(organizationId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOrganizationAdmin(User user, Long organizationId) {
        Set<OrganizationMember> organizations = user.getOrganizations();
        for (OrganizationMember organizationMember : user.getOrganizations()) {
            if (organizationMember.isAdmin() && organizationMember.getKey().getOrganization().getId().equals(organizationId)) {
                return true;
            }
        }
        return false;
    }

    public boolean canGetChallenge(User user, Long challengeId, Long organizationId){
        if(organizationId!=null){
            return isOrganizationMember(user, organizationId);
        }
        for(Challenge challenge: user.getInvitedChallenges()){
            if(challenge.getId() == challengeId){
                return true;
            }
        }
        return false;
    }

    public boolean canGetTask(User user, Long taskId, Long organizationId){
        if(organizationId!=null){
            return isOrganizationMember(user, organizationId);
        }
        return true;
    }
}
