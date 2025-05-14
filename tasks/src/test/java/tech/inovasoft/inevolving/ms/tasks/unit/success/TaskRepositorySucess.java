package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.implementation.TaskRepositoryImplementation;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.JpaRepositoryInterface;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskRepositorySucess {

    @Mock
    private JpaRepositoryInterface repository;

    @InjectMocks
    private TaskRepositoryImplementation taskRepository;

    @Test
    public void findById() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
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
        var result = taskRepository.findById(idUser, idTask);

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
    public void saveInDataBase() throws DataBaseException {
        // Given (Dado)
        UUID idTask = UUID.randomUUID();
        Task expectedTask = new Task();
        expectedTask.setNameTask("Name Task");
        expectedTask.setDescriptionTask("Description Task");
        expectedTask.setStatus(Status.TODO);
        expectedTask.setDateTask(Date.valueOf("2025-05-12"));
        expectedTask.setId(idTask);

        Task newTask = new Task();
        expectedTask.setNameTask("Name Task");
        expectedTask.setDescriptionTask("Description Task");
        expectedTask.setStatus(Status.TODO);
        expectedTask.setDateTask(Date.valueOf("2025-05-12"));

        // When (Quando)
        when(repository.save(any(Task.class))).thenReturn(expectedTask);
        var result = taskRepository.saveInDataBase(newTask);

        // Then (Então)
        assertNotNull(result);
        assertEquals(idTask, result.getId());
        assertEquals(expectedTask.getNameTask(), result.getNameTask());
        assertEquals(expectedTask.getDescriptionTask(), result.getDescriptionTask());
        assertEquals(expectedTask.getStatus(), result.getStatus());
        assertEquals(expectedTask.getDateTask(), result.getDateTask());
        assertEquals(expectedTask.getIdObjective(), result.getIdObjective());
        assertEquals(expectedTask.getIdUser(), result.getIdUser());

        verify(repository, times(1)).save(newTask);
    }

    @Test
    public void addNewTaskCopy() throws UserWithoutAuthorizationAboutTheTaskException, NotFoundException, DataBaseException {
        // Given (Dado)
        LocalDate currentDate = LocalDate.now();
        Task existingTask = new Task();
        existingTask.setId(UUID.randomUUID());
        existingTask.setNameTask("Existing Task");
        existingTask.setDescriptionTask("Existing Description");
        existingTask.setStatus(Status.TODO);
        existingTask.setDateTask(Date.valueOf(currentDate));
        existingTask.setIdUser(UUID.randomUUID());

        Task newTask = new Task();
        newTask.setNameTask("New Task");
        newTask.setDescriptionTask("New Description");
        newTask.setStatus(Status.TODO);
        newTask.setDateTask(Date.valueOf("2025-05-08"));
        newTask.setId(existingTask.getId());
        newTask.setIdUser(existingTask.getIdUser());

        // When (Quando)
        when(repository.save(any(Task.class))).thenReturn(existingTask);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(existingTask));

        // Then (Então)
        var result = taskRepository.addNewTaskCopy(newTask, currentDate);

        assertTrue(result);
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    public void deleteTask() throws  DataBaseException {
        // Given (Dado)
        Task task = new Task();

        // When (Quando)
        doNothing().when(repository).delete(any(Task.class));
        var result = taskRepository.deleteTask(task);

        // Then (Então)
        assertNotNull(result);
        assertEquals("Successfully delete task", result.message());

        verify(repository, times(1)).delete(task);
    }

    @Test
    public void findAllIsCopyTaskDate() throws DataBaseException {
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
        when(repository.findAllByIdOriginalTaskAndIsCopy(any(UUID.class), any(Date.class))).thenReturn(List.of(task));
        var result = taskRepository.findAllIsCopyTask(idTask, Date.valueOf("2025-05-12"));

        // Then (Então)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getId(), result.getFirst().getId());

        verify(repository, times(1)).findAllByIdOriginalTaskAndIsCopy(any(UUID.class), any(Date.class));
    }

    @Test
    public void findAllIsCopyTask() throws DataBaseException{
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
        when(repository.findAllByIdOriginalTaskAndIsCopy(any(UUID.class))).thenReturn(List.of(task));
        var result = taskRepository.findAllIsCopyTask(idTask);

        // Then (Então)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getId(), result.getFirst().getId());

        verify(repository, times(1)).findAllByIdOriginalTaskAndIsCopy(any(UUID.class));
    }

    @Test
    public void findAllByIdObjective() throws DataBaseException {
        // Given (Dado)
        var idObjective = UUID.randomUUID();

        List<Task> expectedTasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            expectedTasks.add(
                    new Task(
                            UUID.randomUUID(),
                            "Name Task",
                            "Description Task",
                            Status.TODO,
                            Date.valueOf("2025-05-12"),
                            null,
                            idObjective,
                            null,
                            null,
                            false,
                            false,
                            false,
                            null
                    )
            );
        }

        // When (Quando)
        when(repository.findAllByIdObjective(any(UUID.class))).thenReturn(expectedTasks);
        var result = taskRepository.findAllByIdObjective(idObjective);

        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTasks.size(), result.size());
    }

    @Test
    public void findAllByIdUserAndDateRange() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var startDate = Date.valueOf("2025-05-12");
        var endDate = Date.valueOf("2025-05-13");

        List<Task> expectedTasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            expectedTasks.add(
                    new Task(
                            UUID.randomUUID(),
                            "Name Task",
                            "Description Task",
                            Status.TODO,
                            Date.valueOf("2025-05-12"),
                            UUID.randomUUID(),
                            idUser,
                            null,
                            null,
                            false,
                            false,
                            false,
                            null
                    )
            );
        }

        // When (Quando)
        when(repository.findAllByIdUserAndDateRange(any(UUID.class), any(Date.class), any(Date.class))).thenReturn(expectedTasks);
        var result = taskRepository.findAllByIdUserAndDateRange(idUser, startDate, endDate);

        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTasks.size(), result.size());
        assertEquals(expectedTasks.getFirst().getId(), result.getFirst().getId());
    }

    @Test
    public void findAllByIdUserAndDate() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date date = Date.valueOf("2025-05-12");
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(
                new Task(
                        UUID.randomUUID(),
                        "Name Task",
                        "Description Task",
                        Status.TODO,
                        Date.valueOf("2025-05-12"),
                        UUID.randomUUID(),
                        idUser,
                        null,
                        null,
                        false,
                        false,
                        false,
                        null
                )
        );

        // When (Quando)
        when(repository.findAllByIdUserAndDate(any(UUID.class), any(Date.class))).thenReturn(expectedTasks);
        var result = taskRepository.findAllByIdUserAndDate(idUser, date);


        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTasks.size(), result.size());
        assertEquals(expectedTasks.getFirst().getId(), result.getFirst().getId());
        assertEquals(date, result.getFirst().getDateTask());

        verify(repository, times(1)).findAllByIdUserAndDate(any(UUID.class), any(Date.class));
    }

    @Test
    public void findAllByIdUserAndStatus() throws DataBaseException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(
                new Task(
                        UUID.randomUUID(),
                        "Name Task",
                        "Description Task",
                        Status.TODO,
                        Date.valueOf("2025-05-12"),
                        UUID.randomUUID(),
                        idUser,
                        null,
                        null,
                        false,
                        false,
                        false,
                        null
                )
        );

        // When (Quando)
        when(repository.findAllByIdUserAndStatus(any(UUID.class), any(String.class))).thenReturn(expectedTasks);
        var result = taskRepository.findAllByIdUserAndStatus(idUser, Status.TODO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTasks.size(), result.size());
        assertEquals(expectedTasks.getFirst().getId(), result.getFirst().getId());
        assertEquals(Status.TODO, result.getFirst().getStatus());

        verify(repository, times(1)).findAllByIdUserAndStatus(any(UUID.class), any(String.class));
    }

}
