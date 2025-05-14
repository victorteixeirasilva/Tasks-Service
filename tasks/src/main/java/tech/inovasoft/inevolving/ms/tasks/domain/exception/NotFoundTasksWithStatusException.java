package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class NotFoundTasksWithStatusException extends Throwable {
    public NotFoundTasksWithStatusException(String status) {
        super("Tasks with status " + status + " not found");
    }
}
