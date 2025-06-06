package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class NotFoundTasksWithObjectiveException extends Exception {
    public NotFoundTasksWithObjectiveException() {
        super("It was not possible to find any task registered with this objective.");
    }
}
