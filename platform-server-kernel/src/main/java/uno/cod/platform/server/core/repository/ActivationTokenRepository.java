package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uno.cod.platform.server.core.domain.ActivationToken;

import java.util.UUID;

/**
 * Created by drogetzer on 12.07.2016.
 */
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, UUID> {

    ActivationToken findOneById(UUID id);
    ActivationToken findOneByEmail(String email);
}
