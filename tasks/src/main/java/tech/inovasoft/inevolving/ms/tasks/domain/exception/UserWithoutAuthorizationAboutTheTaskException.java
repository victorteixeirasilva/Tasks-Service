package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class UserWithoutAuthorizationAboutTheTaskException extends Exception {
    public UserWithoutAuthorizationAboutTheTaskException() {
        super("User without authorization about the task.");
    }

    public UserWithoutAuthorizationAboutTheTaskException(String message) {
        super(message);
    }
}
