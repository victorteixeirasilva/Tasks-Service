package tech.inovasoft.inevolving.ms.tasks;

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
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceSuccess {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    public void addTaskWithObjective() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var taskDTO = new RequestTaskDTO(
                "nameTask",
                "descriptionTask",
                LocalDate.of(2025, 1, 1),
//                Date.valueOf("2025-01-01"),
                Optional.of(idObjective),
                idUser
        );

        Task expectedTask = new Task(
            UUID.randomUUID(),
            taskDTO.nameTask(),
            taskDTO.descriptionTask(),
            Status.TODO,
            Date.valueOf(taskDTO.dateTask()),
            idObjective,
            idUser,
            null,
            null,
            false,
            false,
            false,
            null
        );

        // When (Quando)
        when(repository.save(any(Task.class))).thenReturn(expectedTask);
        var result = service.addTask(taskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTask.getId(), result.id());
        assertEquals(expectedTask.getNameTask(), result.nameTask());
        assertEquals(expectedTask.getDescriptionTask(), result.descriptionTask());
        assertEquals(expectedTask.getStatus(), result.status());
        assertEquals(expectedTask.getDateTask(), result.dateTask());
        assertEquals(expectedTask.getIdObjective(), result.idObjective());
        assertEquals(expectedTask.getIdUser(), result.idUser());

        verify(repository, times(1)).save(any());
    }

    @Test
    public void addTaskObjectiveIsEmpty() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var taskDTO = new RequestTaskDTO(
                "nameTask",
                "descriptionTask",
                LocalDate.of(2025, 1, 1),
                Optional.empty(),
                idUser
        );

        Task expectedTask = new Task(
            UUID.randomUUID(),
            taskDTO.nameTask(),
            taskDTO.descriptionTask(),
            Status.TODO,
            Date.valueOf(taskDTO.dateTask()),
            idObjective,
            idUser,
            null,
            null,
            false,
            false,
            false,
            null
        );

        // When (Quando)
        when(repository.save(any(Task.class))).thenReturn(expectedTask);
        var result = service.addTask(taskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTask.getId(), result.id());
        assertEquals(expectedTask.getNameTask(), result.nameTask());
        assertEquals(expectedTask.getDescriptionTask(), result.descriptionTask());
        assertEquals(expectedTask.getStatus(), result.status());
        assertEquals(expectedTask.getDateTask(), result.dateTask());
        assertEquals(expectedTask.getIdObjective(), result.idObjective());
        assertEquals(expectedTask.getIdUser(), result.idUser());

        verify(repository, times(1)).save(any());
    }

    @Test
    public void findById() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        // Given (Dado)
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();

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
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(task));
        var result = service.findById(idUser, idTask);

        // Then (Então)
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getNameTask(), result.getNameTask());
        assertEquals(task.getDescriptionTask(), result.getDescriptionTask());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getDateTask(), result.getDateTask());
        assertEquals(task.getIdObjective(), result.getIdObjective());
        assertEquals(task.getIdUser(), result.getIdUser());

        verify(repository, times(1)).findById(idTask);
    }

    @Test
    public void repeatTask() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
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
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(new Task(
                UUID.randomUUID(),
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
        ));
        ResponseRepeatTaskDTO result = service.repeatTask(idUser, idTask, daysOfTheWeekDTO, startDate, endDate);

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully repeated tasks", result.message());
        assertEquals(4, result.numberRepetitions());

        verify(repository, times(1)).findById(idTask);
        verify(repository, times(3)).save(any());
    }

    @Test
    public void updateTask() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {

        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        RequestUpdateTaskDTO requestUpdateTaskDTO = new RequestUpdateTaskDTO(
            "nameTask",
            "descriptionTask",
            Optional.of(idObjective)
        );

        Task oldTask = new Task(
                UUID.randomUUID(),
                requestUpdateTaskDTO.nameTask(),
                requestUpdateTaskDTO.descriptionTask(),
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
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(oldTask));
        oldTask.setIdObjective(idObjective);
        when(repository.save(any(Task.class))).thenReturn(oldTask);
        var result = service.updateTask(idUser, oldTask.getId(), requestUpdateTaskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(oldTask.getId(), result.id());
        assertEquals(oldTask.getNameTask(), result.nameTask());
        assertEquals(oldTask.getDescriptionTask(), result.descriptionTask());
        assertEquals(oldTask.getStatus(), result.status());
        assertEquals(oldTask.getDateTask(), result.dateTask());
        assertEquals(oldTask.getIdObjective(), result.idObjective());
        assertEquals(oldTask.getIdUser(), result.idUser());

        verify(repository, times(1)).findById(any(UUID.class));
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    public void updateTasksAndTheirFutureRepetitions() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        // Given (Dado)
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-12");
        Date endDate = Date.valueOf("2025-05-18");
        DaysOfTheWeekDTO daysOfTheWeekDTO = new DaysOfTheWeekDTO(true, false, true, false, true, true, false);
        var task = new Task(
                idTask,
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

        RequestUpdateRepeatTaskDTO requestUpdateRepeatTaskDTO = new RequestUpdateRepeatTaskDTO(
                "nameTask",
                "descriptionTask",
                Optional.empty(),
                daysOfTheWeekDTO
        );

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(repository.findAllByIdOriginalTaskAndIsCopy(idTask)).thenReturn(tasks);
        when(repository.save(any(Task.class))).thenReturn(new Task(
                UUID.randomUUID(),
                "nameTask",
                "descriptionTask",
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
        ));
        ResponseUpdateRepeatTaskDTO result = service.updateTasksAndTheirFutureRepetitions(idUser, idTask, startDate, endDate, requestUpdateRepeatTaskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully update repeated tasks", result.message());
        assertEquals(8, result.numberRepetitions());
        assertEquals(4, result.numberUpdateRepetitions());
        assertEquals(4, result.numberDeleteRepetitions());
        assertEquals(0, result.numberCreateRepetitions());

        verify(repository, times(1)).findById(idTask);
        verify(repository, times(4)).save(any());
    }

    @Test
    public void updateTasksCopyAndTheirFutureRepetitions() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
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
                "Name Task",
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
                Optional.empty(),
                daysOfTheWeekDTO
        );

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(new Task(UUID.randomUUID(),
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
                "Name Task",
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
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(taskCopy));
        when(repository.findAllByIdOriginalTaskAndIsCopy(idTask)).thenReturn(tasks);
        when(repository.save(any(Task.class))).thenReturn(new Task(
                UUID.randomUUID(),
                "nameTask",
                "descriptionTask",
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
        ));
        ResponseUpdateRepeatTaskDTO result = service.updateTasksAndTheirFutureRepetitions(idUser, idCopyTask, startDate, endDate, requestUpdateRepeatTaskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully update repeated tasks", result.message());
        assertEquals(8, result.numberRepetitions());
        assertEquals(4, result.numberUpdateRepetitions());
        assertEquals(4, result.numberDeleteRepetitions());
        assertEquals(0, result.numberCreateRepetitions());

        verify(repository, times(4)).save(any());
        verify(repository, times(4)).delete(any());
    }

    @Test
    public void updateTaskStatus() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
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

        task.setStatus(Status.IN_PROGRESS);

        // When (Quando)
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(task);
        ResponseTaskDTO result = service.updateTaskStatus(idUser, idTask, Status.IN_PROGRESS);
        // Then (Então)

        assertNotNull(result);
        assertEquals(Status.IN_PROGRESS, result.status());
        assertEquals(task.getNameTask(), result.nameTask());
        assertEquals(task.getDescriptionTask(), result.descriptionTask());
        assertEquals(task.getId(), result.id());
        assertEquals(task.getDateTask(), result.dateTask());
        assertEquals(task.getIdObjective(), result.idObjective());
        assertEquals(task.getIdUser(), result.idUser());

        verify(repository, times(1)).findById(idTask);
        verify(repository, times(1)).save(any());
    }

    @Test
    public void deleteTask() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
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

        // When (Quando)
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(task));
        doNothing().when(repository).delete(any(Task.class));
        ResponseMessageDTO result = service.deleteTask(idUser, idTask);
        // Then (Então)

        assertNotNull(result);
        assertEquals("Successfully delete task", result.message());

        verify(repository, times(1)).findById(idTask);
        verify(repository, times(1)).delete(task);
    }

    @Test
    public void deleteTasksAndTheirFutureRepetitions(){
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
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(repository.findAllByIdOriginalTaskAndIsCopy(idTask)).thenReturn(tasks);
        doNothing().when(repository).delete(any(Task.class));
        ResponseDeleteTasksDTO result = service.deleteTasksAndTheirFutureRepetitions(idUser, idTask, task.getDateTask());

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully delete tasks", result.message());
        assertEquals(6, result.numberOfDeletedTasks());
    }

    // Given (Dado)
    // When (Quando)
    // Then (Então)


}
