package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.sql.Date;
import java.util.UUID;

public record ResponseTaskDTO(
        UUID id,
        String nameTask,
        String descriptionTask,
        String status,
        Date dateTask,
        UUID idObjective,
        UUID idUser,
        String cancellationReason
) {
    // Construtor que recebe um Task e delega para o construtor canônico
    public ResponseTaskDTO(Task task) {
        this(
                task.getId(),
                task.getNameTask(),
                task.getDescriptionTask(),
                task.getStatus(),
                task.getDateTask(),
                task.getIdObjective(),
                task.getIdUser(),
                task.getCancellationReason()
        );
    }
}