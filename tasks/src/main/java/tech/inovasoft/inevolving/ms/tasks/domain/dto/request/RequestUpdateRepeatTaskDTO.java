package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import java.util.Optional;
import java.util.UUID;

public record RequestUpdateRepeatTaskDTO(
        String nameTask,
        String descriptionTask,
        Optional<UUID> idObjective,
        DaysOfTheWeekDTO daysOfTheWeekDTO
) {
}
