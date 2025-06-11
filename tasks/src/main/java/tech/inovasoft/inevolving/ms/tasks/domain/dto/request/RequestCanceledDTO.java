package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public record RequestCanceledDTO(
        UUID idUser,
        UUID idTask,
        String cancellationReason
) {
}
