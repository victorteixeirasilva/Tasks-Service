package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import java.util.Optional;

public record RequestUpdateRepeatTaskDTO(
        String nameTask,
        String descriptionTask,
        String idObjective,
        DaysOfTheWeekDTO daysOfTheWeekDTO
) {
}
