package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;



import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record RequestTaskDTO(
        String nameTask,
        String descriptionTask,
        LocalDate dateTask,
        UUID idObjective,
        UUID idUser
) {
    public RequestTaskDTO(Task task) {
        this(
                task.getNameTask(),
                task.getDescriptionTask(),
                task.getDateTask().toLocalDate(),
                task.getIdObjective(),
                task.getIdUser()
        );
    }
}
