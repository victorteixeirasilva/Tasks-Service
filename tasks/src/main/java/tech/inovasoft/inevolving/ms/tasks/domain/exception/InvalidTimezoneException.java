package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class InvalidTimezoneException extends Exception {
    public InvalidTimezoneException(String timezone) {
        super("Invalid timezone: " + timezone);
    }
}
