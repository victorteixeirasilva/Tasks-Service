package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public record RequestUpdateDateTaskDTO(
        LocalDate dateTask,
        UUID idTask,
        UUID idUser
) {
}
