package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.domain.Endpoint;
import uno.cod.platform.server.core.domain.Organization;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.dto.task.TaskCreateDto;
import uno.cod.platform.server.core.dto.task.TaskShowDto;
import uno.cod.platform.server.core.mapper.TaskMapper;
import uno.cod.platform.server.core.repository.EndpointRepository;
import uno.cod.platform.server.core.repository.OrganizationRepository;
import uno.cod.platform.server.core.repository.TaskRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository repository;
    private final EndpointRepository endpointRepository;
    private final OrganizationRepository organizationRepository;

    @Autowired
    public TaskService(TaskRepository repository, EndpointRepository endpointRepository, OrganizationRepository organizationRepository) {
        this.repository = repository;
        this.endpointRepository = endpointRepository;
        this.organizationRepository = organizationRepository;
    }

    public void save(TaskCreateDto dto) {
        Endpoint endpoint = endpointRepository.findOne(dto.getEndpointId());
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint.invalid");
        }
        Organization organization= organizationRepository.findOne(dto.getOrganizationId());
        if (organization == null) {
            throw new IllegalArgumentException("organization.invalid");
        }
        Task task = new Task();
        task.setName(dto.getName());
        task.setInstructions(dto.getInstructions());
        task.setDescription(dto.getDescription());
        task.setPublic(dto.isPublic());
        task.setDuration(dto.getDuration());
        endpoint.addTask(task);
        organization.addTask(task);
        repository.save(task);
    }

    public TaskShowDto findById(Long id) {
        return TaskMapper.map(repository.findOne(id));
    }

    public List<TaskShowDto> findAll(Long organizationId) {
        return TaskMapper.map(repository.findAllWithEndpoints(organizationId));
    }
}
