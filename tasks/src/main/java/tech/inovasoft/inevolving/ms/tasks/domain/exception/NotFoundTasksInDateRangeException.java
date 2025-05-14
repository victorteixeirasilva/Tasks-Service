package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class NotFoundTasksInDateRangeException extends Exception {
    public NotFoundTasksInDateRangeException() {
        super("Tasks not found in date range");
    }

    public NotFoundTasksInDateRangeException(String message) {
        super(message);
    }
}
