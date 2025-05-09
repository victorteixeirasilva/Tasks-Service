package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

public record ResponseDeleteTasksDTO(String message, int numberOfDeletedTasks) {
    public ResponseDeleteTasksDTO(String message, int numberOfDeletedTasks) {
        this.message = message;
        this.numberOfDeletedTasks = numberOfDeletedTasks;
    }

    public ResponseDeleteTasksDTO(int numberOfDeletedTasks) {
        this(
                "Successfully delete tasks",
                numberOfDeletedTasks
        );
    }
}
