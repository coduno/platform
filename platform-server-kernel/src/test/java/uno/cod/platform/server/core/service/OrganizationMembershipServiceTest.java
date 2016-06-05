package uno.cod.platform.server.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
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
import uno.cod.platform.server.core.service.util.OrganizationTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

public class OrganizationMembershipServiceTest {
    private OrganizationMembershipService service;
    private OrganizationMembershipRepository repository;
    private UserRepository userRepository;
    private OrganizationRepository organizationRepository;

    @Before
    public void setUp() throws Exception {
        this.repository = Mockito.mock(OrganizationMembershipRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.organizationRepository = Mockito.mock(OrganizationRepository.class);

        this.service = new OrganizationMembershipService(repository, userRepository, organizationRepository);
    }

    @Test
    public void save() throws Exception {
        User user = UserTestUtil.getUser();
        Organization organization = OrganizationTestUtil.getOrganization();
        OrganizationMembershipKey key = new OrganizationMembershipKey();
        key.setUser(user);
        key.setOrganization(organization);

        OrganizationMembershipCreateDto createDto = new OrganizationMembershipCreateDto();
        createDto.setUserId(user.getId());

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(user);
        Mockito.when(organizationRepository.findOne(organization.getId())).thenReturn(organization);
        Mockito.when(repository.findOne(key)).thenReturn(null);

        service.save(createDto, organization.getId());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void saveUserInvalid() throws Exception {
        User user = UserTestUtil.getUser();
        Organization organization = OrganizationTestUtil.getOrganization();
        OrganizationMembershipKey key = new OrganizationMembershipKey();
        key.setUser(user);
        key.setOrganization(organization);

        OrganizationMembershipCreateDto createDto = new OrganizationMembershipCreateDto();
        createDto.setUserId(user.getId());

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(null);
        Mockito.when(organizationRepository.findOne(organization.getId())).thenReturn(organization);
        Mockito.when(repository.findOne(key)).thenReturn(null);

        service.save(createDto, organization.getId());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void saveOrganizationInvalid() throws Exception {
        User user = UserTestUtil.getUser();
        Organization organization = OrganizationTestUtil.getOrganization();
        OrganizationMembershipKey key = new OrganizationMembershipKey();
        key.setUser(user);
        key.setOrganization(organization);

        OrganizationMembershipCreateDto createDto = new OrganizationMembershipCreateDto();
        createDto.setUserId(user.getId());

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(user);
        Mockito.when(organizationRepository.findOne(organization.getId())).thenReturn(null);
        Mockito.when(repository.findOne(key)).thenReturn(null);

        service.save(createDto, organization.getId());
    }

    @Test(expected = CodunoResourceConflictException.class)
    public void saveMembershipExisting() throws Exception {
        User user = UserTestUtil.getUser();
        Organization organization = OrganizationTestUtil.getOrganization();
        OrganizationMembershipKey key = new OrganizationMembershipKey();
        key.setUser(user);
        key.setOrganization(organization);

        OrganizationMembershipCreateDto createDto = new OrganizationMembershipCreateDto();
        createDto.setUserId(user.getId());

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(user);
        Mockito.when(organizationRepository.findOne(organization.getId())).thenReturn(organization);
        Mockito.when(repository.findOne(key)).thenReturn(new OrganizationMembership());

        service.save(createDto, organization.getId());
    }

    @Test
    public void delete() throws Exception {
        User user = UserTestUtil.getUser();
        Organization organization = OrganizationTestUtil.getOrganization();

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(user);
        Mockito.when(organizationRepository.findOne(organization.getId())).thenReturn(organization);

        service.delete(user.getId(), organization.getId());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void deleteUserInvalid() throws Exception {
        User user = UserTestUtil.getUser();
        Organization organization = OrganizationTestUtil.getOrganization();

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(user);
        Mockito.when(organizationRepository.findOne(organization.getId())).thenReturn(null);

        service.delete(user.getId(), organization.getId());
    }

    @Test(expected = CodunoIllegalArgumentException.class)
    public void deleteOrganizationInvalid() throws Exception {
        User user = UserTestUtil.getUser();
        Organization organization = OrganizationTestUtil.getOrganization();

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(null);
        Mockito.when(organizationRepository.findOne(organization.getId())).thenReturn(organization);

        service.delete(user.getId(), organization.getId());
    }
}