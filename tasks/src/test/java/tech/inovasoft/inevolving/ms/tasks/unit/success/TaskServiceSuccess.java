package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.*;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.*;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.JpaRepositoryInterface;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.RecurringTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceSuccess {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SimpleTaskService simpleTaskService;

    @InjectMocks
    private TaskService service;


    @Test
    public void lockTaskByObjective() throws DataBaseException, NotFoundException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 13);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    idObjective,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdObjective(idObjective)).thenReturn(tasks);
        when(simpleTaskService.deleteTask(any(UUID.class), any(UUID.class))).thenReturn(new ResponseMessageDTO(""));
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(new Task());
        var result = service.lockTaskByObjective(idUser, idObjective, Date.valueOf("2025-05-17"));

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks locked!", result.message());

//        verify(taskRepository, times(1)).findAllByIdObjective(idObjective);
        verify(simpleTaskService, times(5)).deleteTask(any(UUID.class), any(UUID.class));
        verify(taskRepository, times(5)).saveInDataBase(any(Task.class));
    }

    @Test
    public void getTasksInDateRange() throws NotFoundTasksInDateRangeException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");
        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndDateRange(idUser, startDate, endDate)).thenReturn(tasks);
        var result = service.getTasksInDateRange(idUser, startDate, endDate);

        // Then (Então)
        assertNotNull(result);
        assertEquals(31, result.size());

        verify(taskRepository, times(1)).findAllByIdUserAndDateRange(idUser, startDate, endDate);
    }

    @Test
    public void getTasksInDate() throws NotFoundTasksInDateException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndDate(idUser, date)).thenReturn(tasks);
        var result = service.getTasksInDate(idUser, date);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());

        verify(taskRepository, times(1)).findAllByIdUserAndDate(idUser, date);
    }

    @Test
    public void getTasksLate() throws NotFoundTasksWithStatusLateException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.LATE,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndStatus(idUser, Status.LATE)).thenReturn(tasks);
        var result = service.getTasksLate(idUser);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());

        verify(taskRepository, times(1)).findAllByIdUserAndStatus(idUser, Status.LATE);
    }

    @Test
    public void getTasksStatusInDateRange() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");
        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.TODO)).thenReturn(tasks);
        var result = service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.TODO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(31, result.size());

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.TODO);
    }

    @Test
    public void getTasksStatusInDateNotFoundTasksWithStatusException() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.TODO)).thenReturn(tasks);
        var result = service.getTasksStatusInDate(idUser, date, Status.TODO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.TODO);
    }

    @Test
    public void getTasksInDateRangeByObjectiveId() throws NotFoundTasksWithObjectiveException, NotFoundException, ExecutionException, InterruptedException, TimeoutException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");
        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    idObjective,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndIdObjectiveAndDateRange(idUser, idObjective, startDate, endDate)).thenReturn(tasks);
        var result = service.getTasksInDateRangeByObjectiveId(idUser, idObjective, startDate, endDate);

        // Then (Então)
        assertNotNull(result);
        assertEquals(31, result.size());

        verify(taskRepository, times(1)).findAllByIdUserAndIdObjectiveAndDateRange(idUser, idObjective,startDate, endDate);
    }

    @Test
    public void getTasksInDateRangeFilteringSubTasks() throws NotFoundTasksInDateRangeException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 20; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }
        for (int i = 21; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndDateRange(idUser, startDate, endDate)).thenReturn(tasks);
        var result = service.getTasksInDateRange(idUser, startDate, endDate);

        // Then (Então)
        assertNotNull(result);
        assertEquals(20, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByIdUserAndDateRange(idUser, startDate, endDate);
    }

    @Test
    public void getTasksInDateRangeByObjectiveIdFilteringSubTasks() throws NotFoundTasksWithObjectiveException, NotFoundException, ExecutionException, InterruptedException, TimeoutException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 20; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    idObjective,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }
        for (int i = 21; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    idObjective,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndIdObjectiveAndDateRange(idUser, idObjective, startDate, endDate)).thenReturn(tasks);
        var result = service.getTasksInDateRangeByObjectiveId(idUser, idObjective, startDate, endDate);

        // Then (Então)
        assertNotNull(result);
        assertEquals(20, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByIdUserAndIdObjectiveAndDateRange(idUser, idObjective, startDate, endDate);
    }

    @Test
    public void getTasksByObjectiveIdFilteringSubTasks() throws NotFoundTasksWithObjectiveException, NotFoundException, ExecutionException, InterruptedException, TimeoutException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 20; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    idObjective,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }
        for (int i = 21; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    idObjective,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndIdObjective(idUser, idObjective)).thenReturn(tasks);
        var result = service.getTasksByObjectiveId(idUser, idObjective);

        // Then (Então)
        assertNotNull(result);
        assertEquals(20, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByIdUserAndIdObjective(idUser, idObjective);
    }

    @Test
    public void getTasksInDateFilteringSubTasks() throws NotFoundTasksInDateException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }
        for (int i = 11; i <= 15; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.TODO,
                    date,
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndDate(idUser, date)).thenReturn(tasks);
        var result = service.getTasksInDate(idUser, date);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByIdUserAndDate(idUser, date);
    }

    @Test
    public void getTasksLateFilteringSubTasks() throws NotFoundTasksWithStatusLateException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.LATE,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }
        for (int i = 11; i <= 15; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.LATE,
                    date,
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByIdUserAndStatus(idUser, Status.LATE)).thenReturn(tasks);
        var result = service.getTasksLate(idUser);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByIdUserAndStatus(idUser, Status.LATE);
    }

    @Test
    public void getTasksToDoInDateRangeFilteringSubTasks() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 20; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }
        for (int i = 21; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.TODO)).thenReturn(tasks);
        var result = service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.TODO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(20, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.TODO);
    }

    @Test
    public void getTasksToDoInDateFilteringSubTasks() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }
        for (int i = 11; i <= 15; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.TODO,
                    date,
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.TODO)).thenReturn(tasks);
        var result = service.getTasksStatusInDate(idUser, date, Status.TODO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.TODO);
    }

    @Test
    public void getTasksInProgressInDateRangeFilteringSubTasks() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 20; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.IN_PROGRESS,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }
        for (int i = 21; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.IN_PROGRESS,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.IN_PROGRESS)).thenReturn(tasks);
        var result = service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.IN_PROGRESS);

        // Then (Então)
        assertNotNull(result);
        assertEquals(20, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.IN_PROGRESS);
    }

    @Test
    public void getTasksInProgressInDateFilteringSubTasks() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.IN_PROGRESS,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }
        for (int i = 11; i <= 15; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.IN_PROGRESS,
                    date,
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.IN_PROGRESS)).thenReturn(tasks);
        var result = service.getTasksStatusInDate(idUser, date, Status.IN_PROGRESS);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.IN_PROGRESS);
    }

    @Test
    public void getTasksDoneInDateRangeFilteringSubTasks() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 20; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.DONE,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }
        for (int i = 21; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.DONE,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.DONE)).thenReturn(tasks);
        var result = service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.DONE);

        // Then (Então)
        assertNotNull(result);
        assertEquals(20, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.DONE);
    }

    @Test
    public void getTasksDoneInDateFilteringSubTasks() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.DONE,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }
        for (int i = 11; i <= 15; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.DONE,
                    date,
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.DONE)).thenReturn(tasks);
        var result = service.getTasksStatusInDate(idUser, date, Status.DONE);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.DONE);
    }

    @Test
    public void getTasksCanceledInDateRangeFilteringSubTasks() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        for (int i = 1; i <= 20; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.CANCELLED,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }
        for (int i = 21; i <= 31; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.CANCELLED,
                    Date.valueOf(currentDate),
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDateRange(idUser, startDate, endDate, Status.CANCELLED)).thenReturn(tasks);
        var result = service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.CANCELLED);

        // Then (Então)
        assertNotNull(result);
        assertEquals(20, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByStatusAndDateRange(idUser, startDate, endDate, Status.CANCELLED);
    }

    @Test
    public void getTasksCanceledInDateFilteringSubTasks() throws NotFoundTasksWithStatusException, DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-14");

        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.CANCELLED,
                    date,
                    null,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }
        for (int i = 11; i <= 15; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "SubTask " + i,
                    "Description " + i,
                    Status.CANCELLED,
                    date,
                    null,
                    idUser,
                    UUID.randomUUID(),
                    null,
                    false,
                    false,
                    false,
                    null,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findAllByStatusAndDate(idUser, date, Status.CANCELLED)).thenReturn(tasks);
        var result = service.getTasksStatusInDate(idUser, date, Status.CANCELLED);

        // Then (Então)
        assertNotNull(result);
        assertEquals(10, result.size());
        assertTrue(result.stream().allMatch(task -> task.getIdParentTask() == null));

        verify(taskRepository, times(1)).findAllByStatusAndDate(idUser, date, Status.CANCELLED);
    }

}
