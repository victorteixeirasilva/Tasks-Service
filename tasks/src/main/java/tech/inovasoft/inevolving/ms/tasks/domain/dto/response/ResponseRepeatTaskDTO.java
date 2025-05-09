package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

public record ResponseRepeatTaskDTO(String message, int numberRepetitions) {
    public ResponseRepeatTaskDTO(int numberRepetitions) {
        this("Successfully repeated tasks", numberRepetitions);
    }
}
