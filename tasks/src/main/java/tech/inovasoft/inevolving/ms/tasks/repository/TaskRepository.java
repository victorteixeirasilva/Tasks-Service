package tech.inovasoft.inevolving.ms.tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
