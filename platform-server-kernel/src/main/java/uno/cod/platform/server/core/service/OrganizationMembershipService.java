package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.domain.OrganizationMembership;
import uno.cod.platform.server.core.domain.OrganizationMembershipKey;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.organization.member.OrganizationMembershipCreateDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.exception.CodunoResourceConflictException;
import uno.cod.platform.server.core.repository.OrganizationMembershipRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
public class OrganizationMembershipService {
    private final OrganizationMembershipRepository repository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationMembershipService(OrganizationMembershipRepository repository, UserRepository userRepository, OrganizationRepository organizationRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    public void save(OrganizationMembershipCreateDto dto, UUID organizationId) {
        User user = userRepository.findOne(dto.getUserId());
        if (user == null) {
            throw new CodunoIllegalArgumentException("user.invalid");
        }
        Organization organization = organizationRepository.findOne(organizationId);
        if (organization == null) {
            throw new CodunoIllegalArgumentException("organization.invalid");
        }
        OrganizationMembershipKey key = new OrganizationMembershipKey(user, organization);
        if (repository.findOne(key) != null) {
            throw new CodunoResourceConflictException("organization.member.exists");
        }
        OrganizationMembership membership = new OrganizationMembership(key);
        membership.setAdmin(dto.isAdmin());
        repository.save(membership);
        user.addOrganizationMembership(membership);
        organization.addOrganizationMembership(membership);
    }

    public void delete(UUID userId, UUID organizationId) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new CodunoIllegalArgumentException("user.invalid");
        }
        Organization organization = organizationRepository.findOne(organizationId);
        if (organization == null) {
            throw new CodunoIllegalArgumentException("organization.invalid");
        }
        OrganizationMembershipKey key = new OrganizationMembershipKey(user, organization);
        repository.delete(key);
    }
}
