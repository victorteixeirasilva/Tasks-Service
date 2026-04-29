package tech.inovasoft.inevolving.ms.tasks.unit.faliure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestPostponeTasksForDayDTO;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Falhas propagadas do repositório durante {@link DateTaskService#postponeTasksForReferenceDay}.
 */
@ExtendWith(MockitoExtension.class)
class DateTaskServicePostponeFaliure {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DateTaskService dateTaskService;

    @Test
    void propagatesDataBaseExceptionWhenListingTodo() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 3, 1);
        Date refSql = Date.valueOf(ref);

        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.TODO))
                .thenThrow(new DataBaseException("(findAllByStatusAndDate)", null));

        assertThrows(DataBaseException.class, () -> dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref)));

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, refSql, Status.TODO);
        verify(taskRepository, never()).findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS);
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void propagatesDataBaseExceptionWhenListingInProgressAfterEmptyTodo() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 3, 2);
        Date refSql = Date.valueOf(ref);

        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.TODO))
                .thenReturn(Collections.emptyList());
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS))
                .thenThrow(new DataBaseException("(findAllByStatusAndDate)", null));

        assertThrows(DataBaseException.class, () -> dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref)));

        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void propagatesDataBaseExceptionWhenListingInProgressAfterTodoSucceeded() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 3, 3);
        Date refSql = Date.valueOf(ref);

        Task todo = new Task(
                UUID.randomUUID(),
                "x",
                "y",
                Status.TODO,
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
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS))
                .thenThrow(new DataBaseException("(findAllByStatusAndDate)", null));

        assertThrows(DataBaseException.class, () -> dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref)));

        verify(taskRepository, times(1)).saveInDataBase(any());
    }

    @Test
    void propagatesDataBaseExceptionWhenSavingTodo() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 3, 4);
        Date refSql = Date.valueOf(ref);

        Task todo = new Task(
                UUID.randomUUID(),
                "x",
                "y",
                Status.TODO,
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
        when(taskRepository.saveInDataBase(any(Task.class))).thenThrow(new DataBaseException("(save)", null));

        assertThrows(DataBaseException.class, () -> dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref)));

        verify(taskRepository, never()).findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS);
    }

    @Test
    void propagatesDataBaseExceptionWhenSavingInProgress() throws DataBaseException {
        UUID idUser = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2026, 3, 5);
        Date refSql = Date.valueOf(ref);

        Task prog = new Task(
                UUID.randomUUID(),
                "x",
                "y",
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
        when(taskRepository.findAllByStatusAndDate(idUser, refSql, Status.IN_PROGRESS)).thenReturn(List.of(prog));
        when(taskRepository.saveInDataBase(any(Task.class))).thenThrow(new DataBaseException("(save)", null));

        assertThrows(DataBaseException.class, () -> dateTaskService.postponeTasksForReferenceDay(
                new RequestPostponeTasksForDayDTO(idUser, ref)));
    }
}
