package tech.inovasoft.inevolving.ms.tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.idOriginalTask = :idOriginalTask AND t.isCopy = true")
    List<Task> findAllByIdOriginalTaskAndIsCopy(@Param("idOriginalTask") UUID idOriginalTask);

}
