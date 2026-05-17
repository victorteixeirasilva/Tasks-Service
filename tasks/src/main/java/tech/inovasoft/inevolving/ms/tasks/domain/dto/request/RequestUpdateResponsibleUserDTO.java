package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import java.util.UUID;

public record RequestUpdateResponsibleUserDTO(
        UUID idTask,
        UUID idUser,
        UUID idResponsibleUser
) {
}
