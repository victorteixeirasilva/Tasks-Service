package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.domain.util.TaskTimestampHelper;

import java.sql.Date;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public record TaskViewDTO(
        UUID id,
        String nameTask,
        String descriptionTask,
        String status,
        Date dateTask,
        UUID idObjective,
        UUID idUser,
        UUID idParentTask,
        UUID idOriginalTask,
        Boolean hasSubtasks,
        Boolean blockedByObjective,
        Boolean isCopy,
        String cancellationReason,
        UUID idResponsibleUser,
        OffsetDateTime createdAt,
        OffsetDateTime inProgressAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {
    public TaskViewDTO(Task task, ZoneId userZone) {
        this(
                task.getId(),
                task.getNameTask(),
                task.getDescriptionTask(),
                task.getStatus(),
                task.getDateTask(),
                task.getIdObjective(),
                task.getIdUser(),
                task.getIdParentTask(),
                task.getIdOriginalTask(),
                task.getHasSubtasks(),
                task.getBlockedByObjective(),
                task.getIsCopy(),
                task.getCancellationReason(),
                task.getIdResponsibleUser(),
                TaskTimestampHelper.toOffsetDateTime(task.getCreatedAt(), userZone),
                TaskTimestampHelper.toOffsetDateTime(task.getInProgressAt(), userZone),
                TaskTimestampHelper.toOffsetDateTime(task.getCompletedAt(), userZone),
                TaskTimestampHelper.toOffsetDateTime(task.getCancelledAt(), userZone)
        );
    }

    public static List<TaskViewDTO> fromList(List<Task> tasks, ZoneId userZone) {
        return tasks.stream().map(task -> new TaskViewDTO(task, userZone)).toList();
    }
}
