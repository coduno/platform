package uno.cod.platform.server.core;

/**
 * Indicates that some type is identifiable using an
 * identifier, but also has a unique canonical name for
 * easier handling. The canonical name can for example
 * be used when building URLs.
 * @param <T> is the type of the identifier being used, for
 *           example {@link java.util.UUID} or {@link String}.
 */
public interface Canonical<T> extends Identifiable<T> {
    String getCanonicalName();
}
