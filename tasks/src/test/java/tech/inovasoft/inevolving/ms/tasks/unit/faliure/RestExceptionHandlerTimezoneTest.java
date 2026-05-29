package tech.inovasoft.inevolving.ms.tasks.unit.faliure;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.inovasoft.inevolving.ms.tasks.controller.RestExceptionHandler;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.InvalidTimezoneException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestExceptionHandlerTimezoneTest {

    @Test
    void handleInvalidTimezoneException_returns400WithExceptionResponse() {
        // Given
        RestExceptionHandler handler = new RestExceptionHandler();
        InvalidTimezoneException exception = new InvalidTimezoneException("Not/A/Zone");

        // When
        ResponseEntity<ExceptionResponse> response = handler.handleInvalidTimezoneException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("InvalidTimezoneException", response.getBody().simpleName());
        assertEquals("Invalid timezone: Not/A/Zone", response.getBody().message());
    }
}
