package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskDefaultResponsibleUserSuccess {

    @Test
    public void constructorFromDto_setsIdResponsibleUserEqualToIdUser() {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        RequestTaskDTO dto = new RequestTaskDTO(
                "Task Name",
                "Task Description",
                LocalDate.of(2025, 5, 16),
                idObjective,
                idUser
        );

        // When
        Task task = new Task(dto);

        // Then
        assertEquals(idUser, task.getIdUser());
        assertEquals(idUser, task.getIdResponsibleUser());
        assertEquals(Status.TODO, task.getStatus());
        assertEquals(false, task.getIsCopy());
    }

    @Test
    public void constructorFromDtoWithOriginalTask_setsIdResponsibleUserEqualToIdUserAndIsCopyTrue() {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        UUID idOriginalTask = UUID.randomUUID();
        RequestTaskDTO dto = new RequestTaskDTO(
                "Copy Name",
                "Copy Description",
                LocalDate.of(2025, 5, 20),
                idObjective,
                idUser
        );

        // When
        Task task = new Task(dto, idOriginalTask);

        // Then
        assertEquals(idUser, task.getIdUser());
        assertEquals(idUser, task.getIdResponsibleUser());
        assertEquals(idOriginalTask, task.getIdOriginalTask());
        assertTrue(task.getIsCopy());
    }
}
