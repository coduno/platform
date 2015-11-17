package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import uno.cod.platform.server.core.domain.IdentifiableEntity;
import uno.cod.platform.server.core.mapper.CollectionMapper;

import java.util.List;

/**
 * Created by vbalan on 11/18/2015.
 */
public abstract class AbstractBaseLoadingService<R extends JpaRepository, E extends IdentifiableEntity, DTO> {
    protected AbstractBaseService<R, E> service;
    protected CollectionMapper<E, DTO> mapper;

    @Autowired
    public AbstractBaseLoadingService(AbstractBaseService<R, E> service, CollectionMapper<E, DTO> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    public DTO findById(Long id){
        return mapper.map(service.findById(id));
    }

    public List<DTO> findAll(){
        return mapper.map(service.findAll());
    }
}
