package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import java.util.UUID;

public record RequestUpdateTaskDTO(
        String nameTask,
        String descriptionTask,
        String idObjective
) {
}
