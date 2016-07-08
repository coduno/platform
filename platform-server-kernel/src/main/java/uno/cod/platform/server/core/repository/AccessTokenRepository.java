package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.AccessToken;
import uno.cod.platform.server.core.domain.User;

import java.util.Set;
import java.util.UUID;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, UUID> {
    @EntityGraph("AccessToken.user.details")
    AccessToken findById(UUID id);

    Set<AccessToken> findByUser(User user);
}
