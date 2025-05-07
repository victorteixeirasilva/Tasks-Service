package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;


import java.util.UUID;

public record RequestTaskDTO(
        String nameTask,
        String descriptionTask,
        DateDTO dateTask,
        String idObjective,
        UUID idUser
) {
}
