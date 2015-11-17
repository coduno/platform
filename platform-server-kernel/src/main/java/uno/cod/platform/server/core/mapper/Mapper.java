package uno.cod.platform.server.core.mapper;

/**
 * Created by vbalan on 11/18/2015.
 */
public interface Mapper<FROM, TO> {
    TO map(FROM from);
}
