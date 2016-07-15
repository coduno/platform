package uno.cod.platform.server.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Endpoint;
import uno.cod.platform.server.core.dto.endpoint.EndpointCreateDto;
import uno.cod.platform.server.core.dto.endpoint.EndpointShowDto;
import uno.cod.platform.server.core.repository.EndpointRepository;
import uno.cod.platform.server.core.service.util.EndpointTestUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EndpointServiceTest {
    private EndpointService service;
    private EndpointRepository repository;

    @Before
    public void setup() {
        this.repository = Mockito.mock(EndpointRepository.class);
        this.service = new EndpointService(repository);
    }

    @Test
    public void findAll() throws Exception {
        List<Endpoint> endpoints = Collections.singletonList(EndpointTestUtil.getEndpoint());

        when(repository.findAll()).thenReturn(endpoints);

        List<EndpointShowDto> dtos = service.findAll();

        assertEquals(dtos.size(), endpoints.size());
        Endpoint endpoint = endpoints.get(0);
        EndpointShowDto dto = dtos.get(0);
        assertEquals(endpoint.getId(), dto.getId());
        assertEquals(endpoint.getName(), dto.getName());
        assertEquals(endpoint.getComponent(), dto.getComponent());
    }

    @Test
    public void createFromDto() throws Exception {
        final String name = "name";
        final String component = "component";

        final EndpointCreateDto dto = new EndpointCreateDto();
        dto.setComponent(component);
        dto.setName(name);

        final Endpoint endpoint = new Endpoint();
        endpoint.setComponent(component);
        endpoint.setName(name);

        when(repository.save(any(Endpoint.class))).thenReturn(endpoint);

        ArgumentCaptor<Endpoint> captor = ArgumentCaptor.forClass(Endpoint.class);
        service.createFromDto(dto);

        verify(repository).save(captor.capture());

        assertEquals(component, captor.getValue().getComponent());
        assertEquals(name, captor.getValue().getName());
    }

    @Test
    public void createFromExistingDto() throws Exception {
        final String component = "component";

        final EndpointCreateDto dto = new EndpointCreateDto();
        dto.setComponent(component);

        final UUID uuid = new UUID(0xcafe, 0xbabe);

        final Endpoint endpoint = new Endpoint(uuid);
        endpoint.setComponent(component);

        when(repository.findOneByComponent(component)).thenReturn(endpoint);

        final UUID result = service.createFromDto(dto);

        assertEquals(uuid, result);
    }
}