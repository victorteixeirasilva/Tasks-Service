package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import java.util.Optional;
import java.util.UUID;

public record RequestUpdateTaskDTO(
        String nameTask,
        String descriptionTask,
        UUID idObjective
) {
}
