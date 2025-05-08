package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;


import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

public record RequestTaskDTO(
        String nameTask,
        String descriptionTask,
        Date dateTask,
        Optional<UUID> idObjective,
        UUID idUser
) {
}
