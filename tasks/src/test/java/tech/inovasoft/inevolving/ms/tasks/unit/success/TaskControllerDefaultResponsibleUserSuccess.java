package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.inovasoft.inevolving.ms.tasks.controller.TaskController;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.service.RecurringTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskControllerDefaultResponsibleUserSuccess {

    private static final String VALID_TOKEN = "valid-token";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String NULL_TOKEN = "null-token";

    @Mock
    private SimpleTaskService simpleTaskService;

    @Mock
    private TaskService taskService;

    @Mock
    private RecurringTaskService recurringTaskService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TaskController taskController;

    @Test
    public void addTask_withValidToken_returns200AndResponseTaskDTO() throws Exception {
        // Given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();

        RequestTaskDTO requestDto = new RequestTaskDTO(
                "Tarefa",
                "Desc",
                LocalDate.of(2025, 5, 16),
                idObjective,
                idUser
        );

        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("consumer", "tasks"));
        when(simpleTaskService.addTask(any())).thenReturn(new ResponseTaskDTO(
                idTask,
                "Tarefa",
                "Desc",
                Status.TODO,
                Date.valueOf("2025-05-16"),
                idObjective,
                idUser,
                null
        ));

        // When
        ResponseEntity<ResponseTaskDTO> response =
                taskController.addTask(requestDto, VALID_TOKEN).get();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(idTask, response.getBody().id());
        assertEquals("Tarefa", response.getBody().nameTask());
        assertEquals("Desc", response.getBody().descriptionTask());
        assertEquals(Status.TODO, response.getBody().status());
        assertEquals(idObjective, response.getBody().idObjective());
        assertEquals(idUser, response.getBody().idUser());
        assertNull(response.getBody().cancellationReason());
    }

    @Test
    public void addTask_withNullTokenValidation_returns401() throws Exception {
        // Given
        RequestTaskDTO requestDto = new RequestTaskDTO(
                "Tarefa",
                "Desc",
                LocalDate.of(2025, 5, 16),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        when(tokenService.validateToken(NULL_TOKEN)).thenReturn(null);

        // When
        ResponseEntity<ResponseTaskDTO> response =
                taskController.addTask(requestDto, NULL_TOKEN).get();

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void addTask_withInvalidToken_returns401() throws Exception {
        // Given
        RequestTaskDTO requestDto = new RequestTaskDTO(
                "Tarefa",
                "Desc",
                LocalDate.of(2025, 5, 16),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        when(tokenService.validateToken(INVALID_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));

        // When
        ResponseEntity<ResponseTaskDTO> response =
                taskController.addTask(requestDto, INVALID_TOKEN).get();

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void addTask_whenServiceThrowsNotFoundException_propagatesException() throws Exception {
        // Given
        RequestTaskDTO requestDto = new RequestTaskDTO(
                "Tarefa",
                "Desc",
                LocalDate.of(2025, 5, 16),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("consumer", "tasks"));
        when(simpleTaskService.addTask(any()))
                .thenThrow(new NotFoundException("Task not found"));

        // When / Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> taskController.addTask(requestDto, VALID_TOKEN).get()
        );
        assertEquals("Task not found", exception.getMessage());
    }
}
