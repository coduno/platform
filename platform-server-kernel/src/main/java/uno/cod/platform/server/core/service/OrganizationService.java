package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.domain.OrganizationMembership;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.organization.OrganizationCreateDto;
import uno.cod.platform.server.core.dto.organization.OrganizationShowDto;
import uno.cod.platform.server.core.dto.organization.member.OrganizationMembershipShowDto;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.mapper.OrganizationMapper;
import uno.cod.platform.server.core.repository.OrganizationMembershipRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipRepository organizationMembershipRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository,
                               OrganizationMembershipRepository organizationMembershipRepository,
                               UserRepository userRepository) {
        this.userRepository = userRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
        this.organizationRepository = organizationRepository;
    }

    public void createFromDto(OrganizationCreateDto dto, String owner) {
        if (organizationRepository.findByCanonicalName(dto.getNick()) != null) {
            throw new CodunoIllegalArgumentException("organization.invalid");
        }
        Organization organization = new Organization(dto.getNick(), dto.getName());
        organization = organizationRepository.save(organization);

        User user = userRepository.findByCanonicalName(owner);

        OrganizationMembership organizationMembership = new OrganizationMembership(user, organization);
        organizationMembership.setAdmin(true);

        organizationMembershipRepository.save(organizationMembership);
    }

    public OrganizationShowDto findById(UUID id) {
        return OrganizationMapper.map(organizationRepository.findOne(id));
    }

    public List<OrganizationMembershipShowDto> findUserOrganizations(String username) {
        User user = userRepository.findByCanonicalNameOrEmail(username, username);
        if (user == null) {
            throw new CodunoIllegalArgumentException("user.invalid");
        }
        if (user.getOrganizationMemberships() == null) {
            return Collections.emptyList();
        }
        return user.getOrganizationMemberships().stream().map(OrganizationMembershipShowDto::new).collect(Collectors.toList());
    }

    public List<OrganizationShowDto> findAll() {
        return OrganizationMapper.map(organizationRepository.findAll());
    }
}
