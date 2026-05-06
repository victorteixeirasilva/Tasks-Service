package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public record RequestSubtaskDTO(
        String nameTask,
        String descriptionTask,
        LocalDate dateTask,
        UUID idParentTask,
        UUID idUser
) {}
