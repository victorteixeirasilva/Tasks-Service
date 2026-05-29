package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestPostponeTasksForDayDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateDateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.DateTaskService;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DateTaskServiceTimestampsSuccess {

    private static final ZoneId USER_ZONE = ZoneId.of("America/Sao_Paulo");

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DateTaskService dateTaskService;

    @Test
    void updateDateTask_preservesTimestampsInResponse() throws DataBaseException, NotFoundException {
        // Given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        Instant created = Instant.parse("2026-05-29T10:00:00Z");
        Instant inProgress = Instant.parse("2026-05-29T11:00:00Z");
        Task task = new Task(
                idTask, "Name", "Desc", Status.IN_PROGRESS, Date.valueOf("2026-05-12"),
                null, idUser, null, null, false, false, false, null, null,
                created, inProgress, null, null
        );
        RequestUpdateDateTaskDTO dto = new RequestUpdateDateTaskDTO(
                LocalDate.of(2026, 6, 1), idTask, idUser
        );
        when(taskRepository.findById(idTask, idUser)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ResponseTaskDTO result = dateTaskService.updateDateTask(dto, USER_ZONE);

        // Then
        assertEquals(OffsetDateTime.ofInstant(created, USER_ZONE), result.createdAt());
        assertEquals(OffsetDateTime.ofInstant(inProgress, USER_ZONE), result.inProgressAt());
        assertNotNull(result.dateTask());
    }

    @Test
    void postponeTasksForReferenceDay_doesNotAlterLifecycleTimestamps() throws DataBaseException {
        // Given
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 5, 1);
        Date refSql = Date.valueOf(ref);
        Instant created = Instant.parse("2026-04-01T08:00:00Z");
        Instant inProgress = Instant.parse("2026-04-02T09:00:00Z");
        Instant completed = Instant.parse("2026-04-03T10:00:00Z");
        Instant cancelled = Instant.parse("2026-04-04T11:00:00Z");

        Task todo = new Task(
                UUID.randomUUID(), "t", "d", Status.TODO, refSql, null, idUser,
                null, null, false, false, false, null, null,
                created, inProgress, completed, cancelled
        );
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.TODO))
                .thenReturn(List.of(todo));
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS))
                .thenReturn(List.of());
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        dateTaskService.postponeTasksForReferenceDay(new RequestPostponeTasksForDayDTO(idUser, ref));

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveInDataBase(captor.capture());
        Task saved = captor.getValue();
        assertEquals(Status.LATE, saved.getStatus());
        assertEquals(created, saved.getCreatedAt());
        assertEquals(inProgress, saved.getInProgressAt());
        assertEquals(completed, saved.getCompletedAt());
        assertEquals(cancelled, saved.getCancelledAt());
    }

    @Test
    void postponeTasksForReferenceDay_inProgress_doesNotAlterLifecycleTimestamps() throws DataBaseException {
        // Given
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 6, 15);
        Date refSql = Date.valueOf(ref);
        Instant created = Instant.parse("2026-06-01T08:00:00Z");

        Task inProgress = new Task(
                UUID.randomUUID(), "p", "d", Status.IN_PROGRESS, refSql, null, idUser,
                null, null, false, false, false, null, null,
                created, Instant.parse("2026-06-02T09:00:00Z"), null, null
        );
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.TODO))
                .thenReturn(List.of());
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS))
                .thenReturn(List.of(inProgress));
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        dateTaskService.postponeTasksForReferenceDay(new RequestPostponeTasksForDayDTO(idUser, ref));

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveInDataBase(captor.capture());
        assertEquals(Status.IN_PROGRESS, captor.getValue().getStatus());
        assertEquals(created, captor.getValue().getCreatedAt());
        verify(taskRepository, times(1)).findAllByStatusAndDate(eq(idUser), eq(refSql), eq(Status.IN_PROGRESS));
    }
}
