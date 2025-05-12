package tech.inovasoft.inevolving.ms.tasks.domain.exception;

public class NotFoundException extends Exception {
    public NotFoundException(String s) {
        super(s);
    }
    public NotFoundException(){
        super("Task not found");
    }

}
