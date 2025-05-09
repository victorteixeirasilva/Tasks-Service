package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;



import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record RequestTaskDTO(
        String nameTask,
        String descriptionTask,
        LocalDate dateTask,
        Optional<UUID> idObjective,
        UUID idUser
) {
}
