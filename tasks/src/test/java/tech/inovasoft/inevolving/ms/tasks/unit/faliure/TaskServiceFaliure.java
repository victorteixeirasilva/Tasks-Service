package tech.inovasoft.inevolving.ms.tasks.unit.faliure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.*;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceFaliure {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SimpleTaskService simpleTaskService;

    @InjectMocks
    private TaskService service;

    @Test
    public void getTasksInDateRangeNotFoundTasksInDateRangeException() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");
        List<Task> tasks = new ArrayList<>();


        // When (Quando)
        when(taskRepository.findAllByIdUserAndDateRange(idUser, startDate, endDate)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksInDateRangeException.class, () -> {
            service.getTasksInDateRange(idUser, startDate, endDate);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks not found in date range", result.getMessage());

        verify(taskRepository, times(1)).findAllByIdUserAndDateRange(idUser, startDate, endDate);
    }

    @Test
    public void getTasksInDateNotFoundTasksInDateException() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");
        List<Task> tasks = new ArrayList<>();

        // When (Quando)
        when(taskRepository.findAllByIdUserAndDate(idUser, date)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksInDateException.class, () -> {
            service.getTasksInDate(idUser, date);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks not found in date", result.getMessage());

        verify(taskRepository, times(1)).findAllByIdUserAndDate(idUser, date);
    }

    @Test
    public void getTasksLateNotFoundTasksWithStatusLateException() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");
        List<Task> tasks = new ArrayList<>();

        // When (Quando)
        when(taskRepository.findAllByIdUserAndStatus(idUser, Status.LATE)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusLateException.class, () -> {
            service.getTasksLate(idUser);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status late not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByIdUserAndStatus(idUser, Status.LATE);
    }

    @Test
    public void getTasksStatusInDateRangeNotFoundTasksWithStatusException() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");
        List<Task> tasks = new ArrayList<>();

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.TODO)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.TODO);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.TODO + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.TODO);
    }

    @Test
    public void getTasksStatusInDateNotFoundTasksWithStatusException() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");
        List<Task> tasks = new ArrayList<>();

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.TODO)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDate(idUser, date, Status.TODO);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.TODO + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.TODO);
    }

    @Test
    public void getTasksInDateRangeAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.TODO, Date.valueOf(currentDate), null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndDateRange(idUser, startDate, endDate)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksInDateRangeException.class, () -> {
            service.getTasksInDateRange(idUser, startDate, endDate);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks not found in date range", result.getMessage());

        verify(taskRepository, times(1)).findAllByIdUserAndDateRange(idUser, startDate, endDate);
    }

    @Test
    public void getTasksInDateRangeByObjectiveIdAllSubTasksThrowsNotFound() throws DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.TODO, Date.valueOf(currentDate), idObjective, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndIdObjectiveAndDateRange(idUser, idObjective, startDate, endDate)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithObjectiveException.class, () -> {
            service.getTasksInDateRangeByObjectiveId(idUser, idObjective, startDate, endDate);
        });

        // Then (Então)
        assertNotNull(result);

        verify(taskRepository, times(1)).findAllByIdUserAndIdObjectiveAndDateRange(idUser, idObjective, startDate, endDate);
    }

    @Test
    public void getTasksByObjectiveIdAllSubTasksThrowsNotFound() throws DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.TODO, Date.valueOf(currentDate), idObjective, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndIdObjective(idUser, idObjective)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithObjectiveException.class, () -> {
            service.getTasksByObjectiveId(idUser, idObjective);
        });

        // Then (Então)
        assertNotNull(result);

        verify(taskRepository, times(1)).findAllByIdUserAndIdObjective(idUser, idObjective);
    }

    @Test
    public void getTasksInDateAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.TODO, date, null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndDate(idUser, date)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksInDateException.class, () -> {
            service.getTasksInDate(idUser, date);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks not found in date", result.getMessage());

        verify(taskRepository, times(1)).findAllByIdUserAndDate(idUser, date);
    }

    @Test
    public void getTasksLateAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.LATE, date, null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndStatus(idUser, Status.LATE)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusLateException.class, () -> {
            service.getTasksLate(idUser);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status late not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByIdUserAndStatus(idUser, Status.LATE);
    }

    @Test
    public void getTasksToDoInDateRangeAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.TODO, Date.valueOf(currentDate), null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.TODO)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.TODO);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.TODO + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.TODO);
    }

    @Test
    public void getTasksToDoInDateAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.TODO, date, null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.TODO)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDate(idUser, date, Status.TODO);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.TODO + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.TODO);
    }

    @Test
    public void getTasksInProgressInDateRangeAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.IN_PROGRESS, Date.valueOf(currentDate), null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.IN_PROGRESS)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.IN_PROGRESS);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.IN_PROGRESS + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.IN_PROGRESS);
    }

    @Test
    public void getTasksInProgressInDateAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.IN_PROGRESS, date, null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.IN_PROGRESS)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDate(idUser, date, Status.IN_PROGRESS);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.IN_PROGRESS + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.IN_PROGRESS);
    }

    @Test
    public void getTasksDoneInDateRangeAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.DONE, Date.valueOf(currentDate), null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.DONE)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.DONE);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.DONE + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.DONE);
    }

    @Test
    public void getTasksDoneInDateAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.DONE, date, null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.DONE)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDate(idUser, date, Status.DONE);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.DONE + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.DONE);
    }

    @Test
    public void getTasksCanceledInDateRangeAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.CANCELLED, Date.valueOf(currentDate), null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.CANCELLED)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.CANCELLED);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.CANCELLED + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.CANCELLED);
    }

    @Test
    public void getTasksCanceledInDateAllSubTasksThrowsNotFound() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(UUID.randomUUID(), "SubTask " + i, "Description " + i, Status.CANCELLED, date, null, idUser, UUID.randomUUID(), null, false, false, false, null, null, null, null, null, null));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.CANCELLED)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksWithStatusException.class, () -> {
            service.getTasksStatusInDate(idUser, date, Status.CANCELLED);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks with status " + Status.CANCELLED + " not found", result.getMessage());

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.CANCELLED);
    }
}
