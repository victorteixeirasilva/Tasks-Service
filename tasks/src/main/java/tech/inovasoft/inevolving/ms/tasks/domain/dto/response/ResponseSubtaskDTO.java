package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.sql.Date;
import java.util.UUID;

public record ResponseSubtaskDTO(
        UUID id,
        String nameTask,
        String descriptionTask,
        String status,
        Date dateTask,
        UUID idObjective,
        UUID idUser,
        UUID idParentTask,
        String cancellationReason
) {
    public ResponseSubtaskDTO(Task task) {
        this(
                task.getId(),
                task.getNameTask(),
                task.getDescriptionTask(),
                task.getStatus(),
                task.getDateTask(),
                task.getIdObjective(),
                task.getIdUser(),
                task.getIdParentTask(),
                task.getCancellationReason()
        );
    }
}
