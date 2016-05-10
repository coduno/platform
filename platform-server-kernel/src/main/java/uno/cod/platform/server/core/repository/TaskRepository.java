package uno.cod.platform.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uno.cod.platform.server.core.domain.Task;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query("SELECT task FROM Task task " +
            "LEFT JOIN FETCH task.endpoint " +
            "LEFT JOIN FETCH task.organization organization " +
            "WHERE organization = null OR organization.id = :organizationId")
    List<Task> findAllByOrganizationIdWithEndpoints(@Param("organizationId") UUID organizationId);

    @Query("SELECT task FROM Task task " +
            "LEFT JOIN FETCH task.endpoint " +
            "LEFT JOIN FETCH task.organization " +
            "LEFT JOIN FETCH task.runner ")
    List<Task> findAllWithAll();

    @Query("SELECT task FROM Task task " +
            "LEFT JOIN FETCH task.tests tests " +
            "LEFT JOIN FETCH tests.runner " +
            "WHERE task.id = :id")
    Task findOneWithTests(@Param("id") UUID id);

    @Query("SELECT task FROM Task task " +
            "LEFT JOIN FETCH task.runner " +
            "WHERE task.id = :id")
    Task findOneWithRunner(@Param("id") UUID id);

    @Query("SELECT task FROM Task task " +
            "LEFT JOIN FETCH task.templates templates " +
            "WHERE task.id = :id")
    Task findOneWithTemplates(@Param("id") UUID id);

    @Query("SELECT task FROM Task task " +
            "LEFT JOIN FETCH task.languages " +
            "LEFT OUTER JOIN FETCH task.templates templates " +
            "LEFT JOIN FETCH templates.language " +
            "WHERE task.id = :id")
    Task findOneWithLanguagesAndTemplates(@Param("id") UUID id);

    @Query("SELECT task FROM Task task " +
            "LEFT JOIN FETCH task.organization " +
            "WHERE task.id = :id")
    Task findOneWithOrganization(@Param("id") UUID id);

    List<Task> findAllByChallengeTemplatesId(UUID challengeTemplate);
}
