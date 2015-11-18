package uno.cod.platform.server.core.mapper;

import uno.cod.platform.server.core.domain.IdentifiableEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vbalan on 11/18/2015.
 */
public abstract class CollectionMapper<FROM extends IdentifiableEntity, TO> implements Mapper<FROM, TO> {
    public abstract TO map(FROM from);

    public List<TO> map(List<FROM> entities) {
        if (entities == null || entities.size() == 0) {
            return null;
        }

        return entities.stream().map(e -> map(e)).collect(Collectors.toList());
    }
}
