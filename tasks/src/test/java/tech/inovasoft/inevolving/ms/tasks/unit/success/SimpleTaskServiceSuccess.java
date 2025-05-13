package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.*;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SimpleTaskServiceSuccess {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private SimpleTaskService simpleTaskService;

    @Test
    public void addTask() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var taskDTO = new RequestTaskDTO(
                "nameTask",
                "descriptionTask",
                LocalDate.of(2025, 1, 1),
                idObjective,
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
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(expectedTask);
        var result = simpleTaskService.addTask(taskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTask.getId(), result.id());
        assertEquals(expectedTask.getNameTask(), result.nameTask());
        assertEquals(expectedTask.getDescriptionTask(), result.descriptionTask());
        assertEquals(expectedTask.getStatus(), result.status());
        assertEquals(expectedTask.getDateTask(), result.dateTask());
        assertEquals(expectedTask.getIdObjective(), result.idObjective());
        assertEquals(expectedTask.getIdUser(), result.idUser());

        verify(taskRepository, times(1)).saveInDataBase(any());
    }

    @Test
    public void updateTask() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {

        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        RequestUpdateTaskDTO requestUpdateTaskDTO = new RequestUpdateTaskDTO(
                "nameTask",
                "descriptionTask",
                idObjective
        );

        ResponseTaskDTO responseTaskDTO = new ResponseTaskDTO(
                idTask,
                "nameTask",
                "descriptionTask",
                Status.TODO,
                Date.valueOf(LocalDate.now()),
                idObjective,
                idUser,
                null
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
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(oldTask);
        oldTask.setIdObjective(idObjective);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(oldTask);
        var result = simpleTaskService.updateTask(idUser, oldTask.getId(), requestUpdateTaskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(oldTask.getId(), result.id());
        assertEquals(oldTask.getNameTask(), result.nameTask());
        assertEquals(oldTask.getDescriptionTask(), result.descriptionTask());
        assertEquals(oldTask.getStatus(), result.status());
        assertEquals(oldTask.getDateTask(), result.dateTask());
        assertEquals(oldTask.getIdObjective(), result.idObjective());
        assertEquals(oldTask.getIdUser(), result.idUser());

        verify(taskRepository, times(1)).findById(any(UUID.class), any(UUID.class));
        verify(taskRepository, times(1)).saveInDataBase(any(Task.class));
    }

    @Test
    public void updateTaskStatus() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
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
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(task);
        ResponseTaskDTO result = simpleTaskService.updateTaskStatus(idUser, idTask, Status.IN_PROGRESS);
        // Then (Então)
        assertNotNull(result);
        assertEquals(Status.IN_PROGRESS, result.status());
        assertEquals(task.getNameTask(), result.nameTask());
        assertEquals(task.getDescriptionTask(), result.descriptionTask());
        assertEquals(task.getId(), result.id());
        assertEquals(task.getDateTask(), result.dateTask());
        assertEquals(task.getIdObjective(), result.idObjective());
        assertEquals(task.getIdUser(), result.idUser());

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, times(1)).saveInDataBase(any());
    }

    @Test
    public void deleteTask() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
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
        when(taskRepository.findById(any(UUID.class), any(UUID.class))).thenReturn(task);
        when(taskRepository.deleteTask(any(Task.class))).thenReturn(new ResponseMessageDTO("Successfully delete task"));
        ResponseMessageDTO result = simpleTaskService.deleteTask(idUser, idTask);
        // Then (Então)

        assertNotNull(result);
        assertEquals("Successfully delete task", result.message());

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, times(1)).deleteTask(task);
    }
}
