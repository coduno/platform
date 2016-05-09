package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph("User.detail")
    User findByUsername(String username);

    @EntityGraph("User.detail")
    User findByEmail(String email);

    User findByUsernameOrEmail(String username, String email);

    @Query("Select user From User user " +
            "Where user.username like :searchValue " +
            "   OR user.email like :searchValue " +
            "   OR user.firstName like :searchValue " +
            "   OR user.lastName like :searchValue")
    List<User> findLikeUsernameOrEmail(@Param("searchValue") String searchValue);

    @Query("SELECT user FROM User user " +
            "LEFT JOIN FETCH user.results results " +
            "LEFT JOIN FETCH results.taskResults " +
            "WHERE user.id = :id")
    User findOneWithResults(@Param("id") UUID id);

    @Query("SELECT user FROM User user " +
            "LEFT JOIN FETCH user.teams " +
            "WHERE user.id = :id")
    User findOneWithTeams(@Param("id") UUID id);
}

