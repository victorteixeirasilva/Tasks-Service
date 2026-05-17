package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.util.UUID;

public record ResponseResponsibleUserDTO(
        UUID idTask,
        UUID idResponsibleUser
) {
    public ResponseResponsibleUserDTO(Task task) {
        this(task.getId(), task.getIdResponsibleUser());
    }
}
