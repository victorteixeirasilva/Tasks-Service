package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.domain.util.TaskTimestampHelper;

import java.sql.Date;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record ResponseTaskDTO(
        UUID id,
        String nameTask,
        String descriptionTask,
        String status,
        Date dateTask,
        UUID idObjective,
        UUID idUser,
        String cancellationReason,
        OffsetDateTime createdAt,
        OffsetDateTime inProgressAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {

    public ResponseTaskDTO(Task task, ZoneId userZone) {
        this(
                task.getId(),
                task.getNameTask(),
                task.getDescriptionTask(),
                task.getStatus(),
                task.getDateTask(),
                task.getIdObjective(),
                task.getIdUser(),
                task.getCancellationReason(),
                TaskTimestampHelper.toOffsetDateTime(task.getCreatedAt(), userZone),
                TaskTimestampHelper.toOffsetDateTime(task.getInProgressAt(), userZone),
                TaskTimestampHelper.toOffsetDateTime(task.getCompletedAt(), userZone),
                TaskTimestampHelper.toOffsetDateTime(task.getCancelledAt(), userZone)
        );
    }
}
