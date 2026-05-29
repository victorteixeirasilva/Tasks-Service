package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.inovasoft.inevolving.ms.tasks.controller.SubtaskController;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.service.SubtaskService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubtaskControllerDefaultResponsibleUserSuccess {

    private static final String VALID_TOKEN = "valid-token";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String NULL_TOKEN = "null-token";
    private static final ZoneId USER_ZONE = ZoneId.of("America/Sao_Paulo");

    @Mock
    private SubtaskService subtaskService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SubtaskController subtaskController;

    @Test
    public void createSubtask_withValidToken_returns200AndResponseSubtaskDTO() throws Exception {
        // Given
        UUID idSubtask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID idParentTask = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();

        RequestSubtaskDTO requestDto = new RequestSubtaskDTO(
                "Subtarefa",
                "Desc",
                LocalDate.of(2025, 5, 16),
                idParentTask,
                idUser
        );

        Task subtask = new Task(
                idSubtask,
                "Subtarefa",
                "Desc",
                Status.TODO,
                Date.valueOf("2025-05-16"),
                idObjective,
                idUser,
                idParentTask,
                null,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("consumer", "tasks"));
        when(subtaskService.createSubtask(any(), any())).thenReturn(new ResponseSubtaskDTO(subtask, USER_ZONE));

        // When
        ResponseEntity<ResponseSubtaskDTO> response =
                subtaskController.createSubtask(requestDto, null, VALID_TOKEN).get();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(idSubtask, response.getBody().id());
        assertEquals("Subtarefa", response.getBody().nameTask());
        assertEquals("Desc", response.getBody().descriptionTask());
        assertEquals(Status.TODO, response.getBody().status());
        assertEquals(idObjective, response.getBody().idObjective());
        assertEquals(idUser, response.getBody().idUser());
        assertEquals(idParentTask, response.getBody().idParentTask());
        assertNull(response.getBody().cancellationReason());
        assertNull(response.getBody().createdAt());
        assertNull(response.getBody().inProgressAt());
        assertNull(response.getBody().completedAt());
        assertNull(response.getBody().cancelledAt());
    }

    @Test
    public void createSubtask_withNullTokenValidation_returns401() throws Exception {
        // Given
        RequestSubtaskDTO requestDto = new RequestSubtaskDTO(
                "Subtarefa",
                "Desc",
                LocalDate.of(2025, 5, 16),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        when(tokenService.validateToken(NULL_TOKEN)).thenReturn(null);

        // When
        ResponseEntity<ResponseSubtaskDTO> response =
                subtaskController.createSubtask(requestDto, null, NULL_TOKEN).get();

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void createSubtask_withInvalidToken_returns401() throws Exception {
        // Given
        RequestSubtaskDTO requestDto = new RequestSubtaskDTO(
                "Subtarefa",
                "Desc",
                LocalDate.of(2025, 5, 16),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        when(tokenService.validateToken(INVALID_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));

        // When
        ResponseEntity<ResponseSubtaskDTO> response =
                subtaskController.createSubtask(requestDto, null, INVALID_TOKEN).get();

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void createSubtask_whenParentNotFound_propagatesException() throws Exception {
        // Given
        RequestSubtaskDTO requestDto = new RequestSubtaskDTO(
                "Subtarefa",
                "Desc",
                LocalDate.of(2025, 5, 16),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("consumer", "tasks"));
        when(subtaskService.createSubtask(any(), any()))
                .thenThrow(new NotFoundException("Parent task not found"));

        // When / Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> subtaskController.createSubtask(requestDto, null, VALID_TOKEN).get()
        );
        assertEquals("Parent task not found", exception.getMessage());
    }
}
