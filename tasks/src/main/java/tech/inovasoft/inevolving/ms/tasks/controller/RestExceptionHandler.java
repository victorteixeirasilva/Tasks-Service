package tech.inovasoft.inevolving.ms.tasks.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.*;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<ExceptionResponse> handleDataBaseException(DataBaseException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(UserWithoutAuthorizationAboutTheTaskException.class)
    public ResponseEntity<ExceptionResponse> handleUserWithoutAuthorizationAboutTheTaskException(UserWithoutAuthorizationAboutTheTaskException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundTasksInDateRangeException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundTasksInDateRangeException(NotFoundTasksInDateRangeException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundTasksInDateException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundTasksInDateException(NotFoundTasksInDateException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundTasksWithStatusLateException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundTasksWithStatusLateException(NotFoundTasksWithStatusLateException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundTasksWithStatusException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundTasksWithStatusException(NotFoundTasksWithStatusException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


}
