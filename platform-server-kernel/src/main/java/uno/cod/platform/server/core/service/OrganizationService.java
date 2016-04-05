package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.domain.OrganizationMembership;
import uno.cod.platform.server.core.domain.OrganizationMembershipKey;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.organization.OrganizationCreateDto;
import uno.cod.platform.server.core.dto.organization.OrganizationShowDto;
import uno.cod.platform.server.core.mapper.OrganizationMapper;
import uno.cod.platform.server.core.repository.OrganizationMembershipRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

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
        Organization organization = new Organization();
        organization.setNick(dto.getNick());
        organization.setName(dto.getName());
        organization = organizationRepository.save(organization);

        User user = userRepository.findByUsername(owner);

        OrganizationMembershipKey organizationMembershipKey = new OrganizationMembershipKey();
        organizationMembershipKey.setOrganization(organization);
        organizationMembershipKey.setUser(user);

        OrganizationMembership organizationMembership = new OrganizationMembership();
        organizationMembership.setKey(organizationMembershipKey);
        organizationMembership.setAdmin(true);

        organizationMembershipRepository.save(organizationMembership);
    }

    public OrganizationShowDto findById(UUID id){
        return OrganizationMapper.map(organizationRepository.findOne(id));
    }

    public OrganizationShowDto findUserAdminOrganization(String username){
        User user = userRepository.findByUsernameOrEmail(username, username);
        for(OrganizationMembership member: user.getOrganizationMemberships()){
            if(member.isAdmin()){
                return OrganizationMapper.map(member.getKey().getOrganization());
            }
        }
        return null;
    }

    public List<OrganizationShowDto> findAll(){
        return OrganizationMapper.map(organizationRepository.findAll());
    }
}
