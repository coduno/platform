package uno.cod.platform.server.core.social;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

class JdbcConnectionRepository implements ConnectionRepository {

    private final String userId;

    private final JdbcTemplate jdbcTemplate;

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final TextEncryptor textEncryptor;

    public JdbcConnectionRepository(String userId, JdbcTemplate jdbcTemplate, ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
        this.userId = userId;
        this.jdbcTemplate = jdbcTemplate;
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
    }

    public MultiValueMap<String, Connection<?>> findAllConnections() {
        List<Connection<?>> resultList = jdbcTemplate.query(selectFromUserConnection() + " where user_id = ? order by provider_id, rank", connectionMapper, userId);
        MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
        Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
        for (String registeredProviderId : registeredProviderIds) {
            connections.put(registeredProviderId, Collections.<Connection<?>>emptyList());
        }
        for (Connection<?> connection : resultList) {
            String providerId = connection.getKey().getProviderId();
            if (connections.get(providerId).size() == 0) {
                connections.put(providerId, new LinkedList<>());
            }
            connections.add(providerId, connection);
        }
        return connections;
    }

    public List<Connection<?>> findConnections(String providerId) {
        return jdbcTemplate.query(selectFromUserConnection() + " where user_id = ? and provider_id = ? order by rank", connectionMapper, userId, providerId);
    }

    @SuppressWarnings("unchecked")
    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
        List<?> connections = findConnections(getProviderId(apiType));
        return (List<Connection<A>>) connections;
    }

    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
        if (providerUsers == null || providerUsers.isEmpty()) {
            throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
        }
        StringBuilder providerUsersCriteriaSql = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("user_id", userId);
        for (Iterator<Entry<String, List<String>>> it = providerUsers.entrySet().iterator(); it.hasNext();) {
            Entry<String, List<String>> entry = it.next();
            String providerId = entry.getKey();
            providerUsersCriteriaSql.append("provider_id = :providerId_").append(providerId).append(" and provider_user_id in (:providerUserIds_").append(providerId).append(")");
            parameters.addValue("providerId_" + providerId, providerId);
            parameters.addValue("providerUserIds_" + providerId, entry.getValue());
            if (it.hasNext()) {
                providerUsersCriteriaSql.append(" or ");
            }
        }
        List<Connection<?>> resultList = new NamedParameterJdbcTemplate(jdbcTemplate).query(selectFromUserConnection() + " where user_id = :userId and " + providerUsersCriteriaSql + " order by provider_id, rank", parameters, connectionMapper);
        MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
        for (Connection<?> connection : resultList) {
            String providerId = connection.getKey().getProviderId();
            List<String> userIds = providerUsers.get(providerId);
            List<Connection<?>> connections = connectionsForUsers.get(providerId);
            if (connections == null) {
                connections = new ArrayList<Connection<?>>(userIds.size());
                for (int i = 0; i < userIds.size(); i++) {
                    connections.add(null);
                }
                connectionsForUsers.put(providerId, connections);
            }
            String providerUserId = connection.getKey().getProviderUserId();
            int connectionIndex = userIds.indexOf(providerUserId);
            connections.set(connectionIndex, connection);
        }
        return connectionsForUsers;
    }

    public Connection<?> getConnection(ConnectionKey connectionKey) {
        try {
            return jdbcTemplate.queryForObject(selectFromUserConnection() + " where user_id = ? and provider_id = ? and provider_user_id = ?", connectionMapper, userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchConnectionException(connectionKey);
        }
    }

    @SuppressWarnings("unchecked")
    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
    }

    @SuppressWarnings("unchecked")
    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
        if (connection == null) {
            throw new NotConnectedException(providerId);
        }
        return connection;
    }

    @SuppressWarnings("unchecked")
    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) findPrimaryConnection(providerId);
    }

    @Transactional
    public void addConnection(Connection<?> connection) {
        try {
            ConnectionData data = connection.createData();
            int rank = jdbcTemplate.queryForObject("select coalesce(max(rank) + 1, 1) as rank from user_connection where user_id = ? and provider_id = ?", new Object[]{userId, data.getProviderId()}, Integer.class);
            jdbcTemplate.update("insert into user_connection (user_id, provider_id, provider_user_id, rank, display_name, profile_url, image_url, access_token, secret, refresh_token, expire_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    userId, data.getProviderId(), data.getProviderUserId(), rank, data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime());
        } catch (DuplicateKeyException e) {
            throw new DuplicateConnectionException(connection.getKey());
        }
    }

    @Transactional
    public void updateConnection(Connection<?> connection) {
        ConnectionData data = connection.createData();
        jdbcTemplate.update("update user_connection set display_name = ?, profile_url = ?, image_url = ?, access_token = ?, secret = ?, refresh_token = ?, expire_time = ? where user_id = ? and provider_id = ? and provider_user_id = ?",
                data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime(), userId, data.getProviderId(), data.getProviderUserId());
    }

    @Transactional
    public void removeConnections(String providerId) {
        jdbcTemplate.update("delete from user_connection where user_id = ? and provider_id = ?", userId, providerId);
    }

    @Transactional
    public void removeConnection(ConnectionKey connectionKey) {
        jdbcTemplate.update("delete from user_connection where user_id = ? and provider_id = ? and provider_user_id = ?", userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
    }

    // internal helpers

    private String selectFromUserConnection() {
        return "select user_id, provider_id, provider_user_id, display_name, profile_url, image_url, access_token, secret, refresh_token, expire_time from user_connection";
    }

    private Connection<?> findPrimaryConnection(String providerId) {
        List<Connection<?>> connections = jdbcTemplate.query(selectFromUserConnection() + " where user_id = ? and provider_id = ? order by rank", connectionMapper, userId, providerId);
        if (connections.size() > 0) {
            return connections.get(0);
        } else {
            return null;
        }
    }

    private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();

    private final class ServiceProviderConnectionMapper implements RowMapper<Connection<?>> {

        public Connection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConnectionData connectionData = mapConnectionData(rs);
            ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
            return connectionFactory.createConnection(connectionData);
        }

        private ConnectionData mapConnectionData(ResultSet rs) throws SQLException {
            return new ConnectionData(rs.getString("provider_id"), rs.getString("provider_user_id"), rs.getString("display_name"), rs.getString("profile_url"), rs.getString("image_url"),
                    decrypt(rs.getString("access_token")), decrypt(rs.getString("secret")), decrypt(rs.getString("refresh_token")), expireTime(rs.getLong("expire_time")));
        }

        private String decrypt(String encryptedText) {
            return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
        }

        private Long expireTime(long expireTime) {
            return expireTime == 0 ? null : expireTime;
        }

    }

    private <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
    }

    private String encrypt(String text) {
        return text != null ? textEncryptor.encrypt(text) : text;
    }

}
