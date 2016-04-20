package uno.cod.platform.server.core.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.User;

import java.util.UUID;

@Service
public class SessionService {
    public void setActiveOrganization(UUID organizationId) {
        ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setActiveOrganization(organizationId);
    }

    public UUID getActiveOrganization() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getActiveOrganization();
    }

    public User getLoggedInUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
