package tech.inovasoft.inevolving.ms.tasks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.*;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity handleDataBaseException(DataBaseException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(UserWithoutAuthorizationAboutTheTaskException.class)
    public ResponseEntity handleUserWithoutAuthorizationAboutTheTaskException(UserWithoutAuthorizationAboutTheTaskException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundTasksInDateRangeException.class)
    public ResponseEntity handleNotFoundTasksInDateRangeException(NotFoundTasksInDateRangeException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundTasksInDateException.class)
    public ResponseEntity handleNotFoundTasksInDateException(NotFoundTasksInDateException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundTasksWithStatusLateException.class)
    public ResponseEntity handleNotFoundTasksWithStatusLateException(NotFoundTasksWithStatusLateException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundTasksWithStatusException.class)
    public ResponseEntity handleNotFoundTasksWithStatusException(NotFoundTasksWithStatusException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


}
