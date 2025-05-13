package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

public record ResponseUpdateRepeatTaskDTO(
        String message
) {
    public ResponseUpdateRepeatTaskDTO(String message) {
        this.message = message;
    }
}
