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
    public void lockTaskByObjective() throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
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

        verify(taskRepository, times(1)).findAllByIdObjective(idObjective);
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
    public void getTasksInDateRangeByObjectiveId() {
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

}
