package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestPostponeTasksForDayDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponsePostponeTasksForDayDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.DateTaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes do fluxo {@link DateTaskService#postponeTasksForReferenceDay} — cenários de sucesso.
 * Mantidos separados dos testes de {@code updateDateTask} para não misturar regressões.
 */
@ExtendWith(MockitoExtension.class)
class DateTaskServicePostponeSuccess {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DateTaskService dateTaskService;

    @Test
    void whenNoTasksOnDay_returnsZeroCountsAndSkipsSave() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 4, 10);
        Date refSql = Date.valueOf(ref);

        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.TODO))
                .thenReturn(Collections.emptyList());
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS))
                .thenReturn(Collections.emptyList());

        ResponsePostponeTasksForDayDTO out = dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref));

        assertEquals(ref, out.referenceDay());
        assertEquals(ref.plusDays(1), out.nextDay());
        assertEquals(0, out.todosMarkedLateAndMoved());
        assertEquals(0, out.inProgressDatesMoved());
        assertEquals(0, out.totalTasksUpdated());
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void todoTasksBecomeLateAndDateMovesOneDay() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 5, 1);
        Date refSql = Date.valueOf(ref);
        Date nextSql = Date.valueOf(ref.plusDays(1));

        Task t1 = minimalTodo(UUID.randomUUID(), idUser, refSql);
        Task t2 = minimalTodo(UUID.randomUUID(), idUser, refSql);

        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.TODO)).thenReturn(List.of(t1, t2));
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS))
                .thenReturn(Collections.emptyList());
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponsePostponeTasksForDayDTO out = dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref));

        assertEquals(2, out.todosMarkedLateAndMoved());
        assertEquals(0, out.inProgressDatesMoved());
        assertEquals(2, out.totalTasksUpdated());
        assertEquals(Status.LATE, t1.getStatus());
        assertEquals(Status.LATE, t2.getStatus());
        assertEquals(nextSql, t1.getDateTask());
        assertEquals(nextSql, t2.getDateTask());
        verify(taskRepository, times(2)).saveInDataBase(any(Task.class));
    }

    @Test
    void inProgressTasksOnlyMoveDate() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 6, 15);
        Date refSql = Date.valueOf(ref);
        Date nextSql = Date.valueOf(ref.plusDays(1));

        Task task = new Task(
                UUID.randomUUID(),
                "n",
                "d",
                Status.IN_PROGRESS,
                refSql,
                null,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );

        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.TODO))
                .thenReturn(Collections.emptyList());
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS)).thenReturn(List.of(task));
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponsePostponeTasksForDayDTO out = dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref));

        assertEquals(0, out.todosMarkedLateAndMoved());
        assertEquals(1, out.inProgressDatesMoved());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals(nextSql, task.getDateTask());
        verify(taskRepository, times(1)).saveInDataBase(task);
    }

    @Test
    void mixedTodoAndInProgress_processesBothGroups() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 7, 20);
        Date refSql = Date.valueOf(ref);

        Task todo = minimalTodo(UUID.randomUUID(), idUser, refSql);
        Task prog = new Task(
                UUID.randomUUID(),
                "p",
                "d",
                Status.IN_PROGRESS,
                refSql,
                null,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );

        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.TODO)).thenReturn(List.of(todo));
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS)).thenReturn(List.of(prog));
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponsePostponeTasksForDayDTO out = dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref));

        assertEquals(1, out.todosMarkedLateAndMoved());
        assertEquals(1, out.inProgressDatesMoved());
        assertEquals(2, out.totalTasksUpdated());
        verify(taskRepository, times(1)).findAllByStatusAndDate(eq(idUser), eq(refSql), eq(Status.TODO));
        verify(taskRepository, times(1)).findAllByStatusAndDate(eq(idUser), eq(refSql), eq(Status.IN_PROGRESS));
        verify(taskRepository, times(2)).saveInDataBase(any(Task.class));
    }

    private static Task minimalTodo(UUID id, UUID idUser, Date dateSql) {
        return new Task(
                id,
                "t",
                "d",
                Status.TODO,
                dateSql,
                null,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );
    }
}
