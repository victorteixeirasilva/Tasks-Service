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
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.JpaRepositoryInterface;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.RecurringTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecurringTaskServiceSuccess {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SimpleTaskService simpleTaskService;

    @InjectMocks
    private RecurringTaskService recurringTaskService;

    @Test
    public void repeatTask() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        // Given (Dado)
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-12");
        Date endDate = Date.valueOf("2025-05-18");
        DaysOfTheWeekDTO daysOfTheWeekDTO = new DaysOfTheWeekDTO(true, false, true, false, true, true, false);
        var task = new Task(
                idTask,
                "Name Task",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-12"),
                null,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );

        // When (Quando)
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(task);

        ResponseRepeatTaskDTO result = recurringTaskService.addTasks(idUser, idTask, daysOfTheWeekDTO, startDate, endDate);

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully repeated tasks", result.message());
        assertEquals(4, result.numberRepetitions());
    }

    @Test
    public void updateTasksAndTheirFutureRepetitions() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        // Given (Dado)
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-12");
        Date endDate = Date.valueOf("2025-05-18");
        DaysOfTheWeekDTO daysOfTheWeekDTO = new DaysOfTheWeekDTO(true, false, true, false, true, true, false);
        var task = new Task(
                idTask,
                "Name Task Original Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-12"),
                idObjective,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );

        RequestUpdateRepeatTaskDTO requestUpdateRepeatTaskDTO = new RequestUpdateRepeatTaskDTO(
                "nameTask",
                "descriptionTask",
                UUID.randomUUID(),
                daysOfTheWeekDTO
        );

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-14"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-16"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-17"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Delete",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-19"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Delete",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-21"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Delete",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-23"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Delete",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-24"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));


        // When (Quando)
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(task);
        when(taskRepository.findAllIsCopyTask(any(UUID.class), any(Date.class))).thenReturn(tasks);
        when(simpleTaskService.deleteTask(any(UUID.class), any(UUID.class))).thenReturn(new ResponseMessageDTO("Successfully delete tasks"));
        ResponseUpdateRepeatTaskDTO result = recurringTaskService.updateTasks(idUser, idTask, endDate, requestUpdateRepeatTaskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully update repeated tasks", result.message());


    }

    @Test
    public void deleteTasksAndTheirFutureRepetitions() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        // Given (Dado)
        var idTask = UUID.randomUUID();
        var idUser = UUID.randomUUID();
        var task = new Task(
                idTask,
                "Name Task",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-12"),
                null,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Name Task",
                    "Description Task",
                    Status.TODO,
                    Date.valueOf("2025-05-13"),
                    null,
                    idUser,
                    null,
                    idTask,
                    false,
                    false,
                    true,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(task);
        when(taskRepository.findAllIsCopyTask(idTask)).thenReturn(tasks);
        ResponseDeleteTasksDTO result = recurringTaskService.deleteTasks(idUser, idTask, task.getDateTask());

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully delete tasks", result.message());
        assertEquals(6, result.numberOfDeletedTasks());
    }

    @Test
    public void deleteTasksCopyAndTheirFutureRepetitions() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        // Given (Dado)
        var idTask = UUID.randomUUID();
        var idUser = UUID.randomUUID();
        var task = new Task(
                idTask,
                "Name Task",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-13"),
                null,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );
        var taskCopy = new Task(
                UUID.randomUUID(),
                "Name Task",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-13"),
                null,
                idUser,
                null,
                idTask,
                false,
                false,
                true,
                null
        );
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        for (int i = 1; i <= 5; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Name Task",
                    "Description Task",
                    Status.TODO,
                    Date.valueOf("2025-05-14"),
                    null,
                    idUser,
                    null,
                    idTask,
                    false,
                    false,
                    true,
                    null
            ));
        }

        // When (Quando)
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(taskCopy);
        when(taskRepository.findAllIsCopyTask(taskCopy.getIdOriginalTask())).thenReturn(tasks);
//        when(taskRepository.deleteTask(any(Task.class))).thenReturn(new ResponseMessageDTO("Successfully delete tasks"));
        ResponseDeleteTasksDTO result = recurringTaskService.deleteTasks(idUser, taskCopy.getId(), taskCopy.getDateTask());

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully delete tasks", result.message());
        assertEquals(6, result.numberOfDeletedTasks());
    }

    @Test
    public void updateTasksCopyAndTheirFutureRepetitions() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        // Given (Dado)
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        UUID idCopyTask = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-12");
        Date endDate = Date.valueOf("2025-05-24");
        DaysOfTheWeekDTO daysOfTheWeekDTO = new DaysOfTheWeekDTO(true, false, true, false, true, true, false);
        var task = new Task(
                UUID.randomUUID(),
                "Name Task",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-12"),
                idObjective,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );

        Task taskCopy = new Task(idCopyTask,
                "Name TaskCopy Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-19"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                true,
                null);

        RequestUpdateRepeatTaskDTO requestUpdateRepeatTaskDTO = new RequestUpdateRepeatTaskDTO(
                "nameTask",
                "descriptionTask",
                UUID.randomUUID(),
                daysOfTheWeekDTO
        );

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
//        tasks.add(taskCopy);
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Deleted date < startDate",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-14"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Deleted date < startDate",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-16"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Deleted date < startDate",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-17"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-21"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-23"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-24"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null));


        // When (Quando)
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(taskCopy);
        when(taskRepository.findAllIsCopyTask(any(UUID.class), any(Date.class))).thenReturn(tasks);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(taskCopy);
        ResponseUpdateRepeatTaskDTO result = recurringTaskService.updateTasks(idUser, idCopyTask, endDate, requestUpdateRepeatTaskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully update repeated tasks", result.message());

    }

    @Test
    public void updateTasksCopyAndTheirFutureRepetitionsAndAddNewTasks() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        // Given (Dado)
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        UUID idCopyTask = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-19");
        Date endDate = Date.valueOf("2025-05-30");
        DaysOfTheWeekDTO daysOfTheWeekDTO = new DaysOfTheWeekDTO(true, true, true, true, true, true, true);
        var task = new Task(
                UUID.randomUUID(),
                "Name Task",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-12"),
                idObjective,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );

        Task taskCopy = new Task(idCopyTask,
                "Name TaskCopy Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-19"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                true,
                null);

        RequestUpdateRepeatTaskDTO requestUpdateRepeatTaskDTO = new RequestUpdateRepeatTaskDTO(
                "nameTask",
                "descriptionTask",
                UUID.randomUUID(),
                daysOfTheWeekDTO
        );

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        Task task1 = new Task(UUID.randomUUID(),
                "Name Task Deleted date < startDate",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-14"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null);
        tasks.add(task1);
        Task task2 = new Task(UUID.randomUUID(),
                "Name Task Deleted date < startDate",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-16"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null);
        tasks.add(task2);
        Task task3 = new Task(UUID.randomUUID(),
                "Name Task Deleted date < startDate",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-17"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null);
        tasks.add(task3);
        Task task4 = new Task(UUID.randomUUID(),
                "Name Task Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-21"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null);
        tasks.add(task4);
        Task task5 = new Task(UUID.randomUUID(),
                "Name Task Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-23"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null);
        tasks.add(task5);
        Task task6 = new Task(UUID.randomUUID(),
                "Name Task Update",
                "Description Task",
                Status.TODO,
                Date.valueOf("2025-05-24"),
                idObjective,
                idUser,
                null,
                idTask,
                false,
                false,
                false,
                null);
        tasks.add(task6);


        // When (Quando)
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(taskCopy);
        when(taskRepository.findAllIsCopyTask(any(UUID.class), any(Date.class))).thenReturn(tasks);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(taskCopy);

        ResponseUpdateRepeatTaskDTO result = recurringTaskService.updateTasks(idUser, idCopyTask, endDate, requestUpdateRepeatTaskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully update repeated tasks", result.message());

    }




    // Given (Dado)
    // When (Quando)
    // Then (Então)


}
