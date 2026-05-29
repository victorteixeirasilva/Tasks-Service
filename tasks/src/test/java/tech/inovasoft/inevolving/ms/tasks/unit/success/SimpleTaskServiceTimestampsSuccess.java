package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestCanceledDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.client.ObjectivesServiceClient;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleTaskServiceTimestampsSuccess {

    private static final ZoneId USER_ZONE = ZoneId.of("America/Sao_Paulo");

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ObjectivesServiceClient objectivesServiceClient;

    @InjectMocks
    private SimpleTaskService simpleTaskService;

    @Test
    void addTask_setsCreatedAtOnSaveAndReturnsConvertedTimestamp() throws Exception {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        RequestTaskDTO dto = new RequestTaskDTO(
                "name", "desc", LocalDate.of(2026, 5, 29), idObjective, idUser
        );
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ResponseTaskDTO result = simpleTaskService.addTask(dto, USER_ZONE);

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveInDataBase(captor.capture());
        assertNotNull(captor.getValue().getCreatedAt());
        assertNotNull(result.createdAt());
        assertEquals(
                OffsetDateTime.ofInstant(captor.getValue().getCreatedAt(), USER_ZONE),
                result.createdAt()
        );
    }

    @Test
    void updateTaskStatus_inProgress_setsInProgressAt() throws DataBaseException, NotFoundException {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        Task task = taskWithId(idTask, idUser);
        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ResponseTaskDTO result = simpleTaskService.updateTaskStatus(idUser, idTask, Status.IN_PROGRESS, USER_ZONE);

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveInDataBase(captor.capture());
        assertNotNull(captor.getValue().getInProgressAt());
        assertNotNull(result.inProgressAt());
    }

    @Test
    void updateTaskStatus_done_setsCompletedAt() throws DataBaseException, NotFoundException {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        Task task = taskWithId(idTask, idUser);
        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ResponseTaskDTO result = simpleTaskService.updateTaskStatus(idUser, idTask, Status.DONE, USER_ZONE);

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveInDataBase(captor.capture());
        assertNotNull(captor.getValue().getCompletedAt());
        assertNotNull(result.completedAt());
    }

    @Test
    void updateTaskStatusCancelled_setsCancelledAt() throws DataBaseException, NotFoundException {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        Task task = taskWithId(idTask, idUser);
        RequestCanceledDTO dto = new RequestCanceledDTO(idUser, idTask, "reason");
        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ResponseTaskDTO result = simpleTaskService.updateTaskStatusCancelled(dto, USER_ZONE);

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveInDataBase(captor.capture());
        assertNotNull(captor.getValue().getCancelledAt());
        assertNotNull(result.cancelledAt());
    }

    @Test
    void updateTask_doesNotAlterStatusTimestamps() throws Exception {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        Instant created = Instant.parse("2026-01-01T10:00:00Z");
        Instant inProgress = Instant.parse("2026-01-02T10:00:00Z");
        Task task = new Task(
                idTask, "n", "d", Status.IN_PROGRESS, Date.valueOf("2026-05-29"),
                null, idUser, null, null, false, false, false, null, null,
                created, inProgress, null, null
        );
        RequestUpdateTaskDTO updateDto = new RequestUpdateTaskDTO("new", "new desc", idObjective);
        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        simpleTaskService.updateTask(idUser, idTask, updateDto, USER_ZONE);

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveInDataBase(captor.capture());
        assertEquals(created, captor.getValue().getCreatedAt());
        assertEquals(inProgress, captor.getValue().getInProgressAt());
        assertNull(captor.getValue().getCompletedAt());
    }

    private static Task taskWithId(UUID idTask, UUID idUser) {
        return new Task(
                idTask, "Name", "Desc", Status.TODO, Date.valueOf("2026-05-29"),
                null, idUser, null, null, false, false, false, null, null,
                null, null, null, null
        );
    }
}
