package tech.inovasoft.inevolving.ms.tasks.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryInterface extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.idOriginalTask = :idOriginalTask AND t.isCopy = true")
    List<Task> findAllByIdOriginalTaskAndIsCopy(@Param("idOriginalTask") UUID idOriginalTask);

    List<Task> findAllByIdObjective(UUID idObjective);

    @Query("SELECT t FROM Task t WHERE t.idOriginalTask = :idOriginalTask AND t.isCopy = true AND t.dateTask >= :specificDate")
    List<Task> findAllByIdOriginalTaskAndIsCopy(
            @Param("idOriginalTask") UUID idOriginalTask,
            @Param("specificDate") Date specificDate);

    @Query("SELECT t FROM Task t WHERE DATE(t.dateTask) = :specificDate AND (t.id = :id OR t.idOriginalTask = :id)")
    Optional<Task> findByIdOriginalTaskOrIdTask(@Param("specificDate") Date specificDate, @Param("id") UUID id);

    @Query("SELECT t FROM Task t WHERE t.idUser = :idUser AND t.dateTask BETWEEN :startDate AND :endDate")
    List<Task> findAllByIdUserAndDateRange(
            @Param("idUser") UUID idUser,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT t FROM Task t WHERE t.idUser = :idUser AND t.dateTask = :date")
    List<Task> findAllByIdUserAndDate(@Param("idUser") UUID idUser, @Param("date") Date date);

    @Query("SELECT t FROM Task t WHERE t.idUser = :idUser AND t.status = :status")
    List<Task> findAllByIdUserAndStatus(@Param("idUser") UUID idUser, @Param("status") String status);

    @Query("SELECT t FROM Task t WHERE t.idUser = :idUser AND t.status = :status AND t.dateTask BETWEEN :startDate AND :endDate")
    List<Task> findAllByStatusAndDateRange(@Param("idUser") UUID idUser, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("status") String status);

    @Query("SELECT t FROM Task t WHERE t.idUser = :idUser AND t.dateTask = :date AND t.status = :status")
    List<Task> findAllByStatusAndDate(@Param("idUser") UUID idUser, @Param("date") Date date, @Param("status") String status);

    @Query("SELECT t FROM Task t WHERE t.idUser = :idUser AND t.idObjective = :idObjective AND t.dateTask BETWEEN :startDate AND :endDate")
    List<Task> findAllByIdUserAndIdObjectiveAndDateRange(
            @Param("idUser") UUID idUser,
            @Param("idObjective") UUID idObjective,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT t FROM Task t WHERE t.idUser = :idUser AND t.idObjective = :idObjective")
    List<Task> findAllByIdUserAndIdObjective(
            @Param("idUser") UUID idUser,
            @Param("idObjective") UUID idObjective
    );
}
