package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class NotFoundTasksInDateException extends Exception {
    public NotFoundTasksInDateException() {
        super("Tasks not found in date");
    }

    public NotFoundTasksInDateException(String message) {
        super(message);
    }
}
