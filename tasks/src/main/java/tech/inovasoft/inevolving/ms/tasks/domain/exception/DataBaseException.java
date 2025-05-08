package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class DataBaseException extends Exception{
    public DataBaseException() {
        super("Error in integration with Database");
    }

    public DataBaseException(String message) {
        super("Error in integration with Database " + message);
    }
}
