package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.organization.OrganizationCreateDto;
import uno.cod.platform.server.core.dto.organization.OrganizationShowDto;
import uno.cod.platform.server.core.dto.organization.member.OrganizationMembershipShowDto;
import uno.cod.platform.server.core.repository.OrganizationMembershipRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.util.OrganizationMembershipTestUtil;
import uno.cod.platform.server.core.service.util.OrganizationTestUtil;
import uno.cod.platform.server.core.service.util.UserTestUtil;

import java.util.Collections;
import java.util.List;

public class OrganizationServiceTest {
    private OrganizationService service;
    private OrganizationRepository repository;
    private OrganizationMembershipRepository organizationMembershipRepository;
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        this.repository = Mockito.mock(OrganizationRepository.class);
        this.organizationMembershipRepository = Mockito.mock(OrganizationMembershipRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);

        this.service = new OrganizationService(repository, organizationMembershipRepository, userRepository);
    }

    @Test
    public void createFromDto() throws Exception {
        User user = UserTestUtil.getUser();
        OrganizationCreateDto dto = new OrganizationCreateDto();
        dto.setNick("nick");
        dto.setName("name");

        Mockito.when(repository.findByNick(dto.getNick())).thenReturn(null);
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        service.createFromDto(dto, user.getUsername());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFromDtoInvalidCanonicalName() throws Exception {
        User user = UserTestUtil.getUser();
        OrganizationCreateDto dto = new OrganizationCreateDto();
        dto.setNick("nick");
        dto.setName("name");

        Mockito.when(repository.findByNick(dto.getNick())).thenReturn(new Organization());
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        service.createFromDto(dto, user.getUsername());
    }

    @Test
    public void findById() throws Exception {
        Organization organization = OrganizationTestUtil.getOrganization();
        Mockito.when(repository.findOne(organization.getId())).thenReturn(organization);

        assertEquals(organization, service.findById(organization.getId()));
    }

    @Test
    public void findUserOrganizations() throws Exception {
        User user = UserTestUtil.getUser();
        Organization organization = OrganizationTestUtil.getOrganization();
        user.addOrganizationMembership(OrganizationMembershipTestUtil.getOrganizationMembership(user, organization, true));
        Mockito.when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())).thenReturn(user);

        List<OrganizationMembershipShowDto> dtos = service.findUserOrganizations(user.getUsername());

        Assert.assertEquals(dtos.size(), 1);
        Assert.assertEquals(dtos.get(0).getId(), organization.getId());
        Assert.assertEquals(dtos.get(0).getName(), organization.getName());
        Assert.assertEquals(dtos.get(0).getNick(), organization.getCanonicalName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findUserOrganizationsNullUser() throws Exception {
        User user = UserTestUtil.getUser();
        Mockito.when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())).thenReturn(null);

        service.findUserOrganizations(user.getUsername());
    }

    @Test
    public void findUserOrganizationsNoMemberships() throws Exception {
        User user = UserTestUtil.getUser();
        Mockito.when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())).thenReturn(user);

        List<OrganizationMembershipShowDto> dtos = service.findUserOrganizations(user.getUsername());

        Assert.assertEquals(dtos.size(), 0);
    }

    @Test
    public void findAll() throws Exception {
        Organization organization = OrganizationTestUtil.getOrganization();
        Mockito.when(repository.findAll()).thenReturn(Collections.singletonList(organization));

        List<OrganizationShowDto> dtos = service.findAll();
        Assert.assertEquals(dtos.size(), 1);
        assertEquals(organization, dtos.get(0));
    }

    private void assertEquals(Organization organization, OrganizationShowDto dto) {
        Assert.assertEquals(dto.getId(), organization.getId());
        Assert.assertEquals(dto.getName(), organization.getName());
        Assert.assertEquals(dto.getNick(), organization.getCanonicalName());
    }
}