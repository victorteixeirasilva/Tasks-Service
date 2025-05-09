package tech.inovasoft.inevolving.ms.tasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.time.LocalDate;
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

    // Given (Dado)
    // When (Quando)
    // Then (Então)

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


}
