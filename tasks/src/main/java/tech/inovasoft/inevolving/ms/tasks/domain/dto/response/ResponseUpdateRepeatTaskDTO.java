package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

public record ResponseUpdateRepeatTaskDTO(
        String message
//        int numberRepetitions,
//        int numberDeleteRepetitions,
//        int numberUpdateRepetitions,
//        int numberCreateRepetitions
) {
//    public ResponseUpdateRepeatTaskDTO(
//            int numberRepetitions,
//            int numberDeleteRepetitions,
//            int numberUpdateRepetitions,
//            int numberCreateRepetitions
//    ) {
//        this("Successfully update repeated tasks", numberRepetitions, numberDeleteRepetitions, numberUpdateRepetitions, numberCreateRepetitions);
//    }


    public ResponseUpdateRepeatTaskDTO(String message) {
        this.message = message;
    }
}
