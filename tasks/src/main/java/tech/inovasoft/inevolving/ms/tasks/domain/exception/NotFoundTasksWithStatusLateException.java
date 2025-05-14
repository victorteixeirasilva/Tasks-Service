package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class NotFoundTasksWithStatusLateException extends Exception {
    public NotFoundTasksWithStatusLateException() {
        super("Tasks with status late not found");
    }

    public NotFoundTasksWithStatusLateException(String message) {
        super(message);
    }
}
