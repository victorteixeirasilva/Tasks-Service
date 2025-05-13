package tech.inovasoft.inevolving.ms.tasks.unit.faliure;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskRepositoryFaliure {

    @Mock
    private JpaRepositoryInterface repository;

    @InjectMocks
    private TaskRepositoryImplementation taskRepository;

    @Test
    public void findByIdDataBaseException() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
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
        when(repository.findById(any(UUID.class))).thenThrow(new RuntimeException());
        Exception result = assertThrows(DataBaseException.class, () -> {
            taskRepository.findById(idUser, idTask);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Error in integration with Database (findById)", result.getMessage());

        verify(repository, times(1)).findById(idTask);
    }

    @Test
    public void findByIdNotFoundException() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
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
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());
        Exception result = assertThrows(NotFoundException.class, () -> {
            taskRepository.findById(idUser, idTask);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("(findById) Task not found", result.getMessage());

        verify(repository, times(1)).findById(idTask);
    }

    @Test
    public void findByIdUserWithoutAuthorizationAboutTheTaskException() throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
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
                UUID.randomUUID(),
                null,
                null,
                false,
                false,
                false,
                null
        );

        // When (Quando)
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(task));
        Exception result = assertThrows(UserWithoutAuthorizationAboutTheTaskException.class, () -> {
            taskRepository.findById(idUser, idTask);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("User without authorization about the task.", result.getMessage());

        verify(repository, times(1)).findById(idTask);
    }

    @Test
    public void saveInDataBase() {
        // Given (Dado)

        // When (Quando)
        when(repository.save(any(Task.class))).thenThrow(new RuntimeException());
        Exception result = assertThrows(DataBaseException.class, () -> {
            taskRepository.saveInDataBase(new Task());
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Error in integration with Database (save)", result.getMessage());

        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    public void addNewTaskCopyDataBaseException() {
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
        when(repository.save(any(Task.class))).thenThrow(new RuntimeException());
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(existingTask));

        // Then (Então)
        Exception result = assertThrows(DataBaseException.class, () -> {
            taskRepository.addNewTaskCopy(newTask, currentDate);
        });

        assertNotNull(result);
        assertEquals("Error in integration with Database (save)", result.getMessage());

        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    public void deleteTaskDataBaseException() {
        // Given (Dado)
        Task task = new Task();

        // When (Quando)
        doThrow(new RuntimeException()).when(repository).delete(any(Task.class));
        Exception result = assertThrows(DataBaseException.class, () -> {
            taskRepository.deleteTask(task);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Error in integration with Database (delete)", result.getMessage());

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
        when(repository.findAllByIdOriginalTaskAndIsCopy(any(UUID.class), any(Date.class))).thenThrow(new RuntimeException());
        var result = assertThrows(DataBaseException.class, () -> {
            taskRepository.findAllIsCopyTask(idTask, Date.valueOf("2025-05-12"));
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Error in integration with Database (findAllByIdOriginalTaskAndIsCopy)", result.getMessage());

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
        when(repository.findAllByIdOriginalTaskAndIsCopy(any(UUID.class))).thenThrow(new RuntimeException());
        var result = assertThrows(DataBaseException.class, () -> {
            taskRepository.findAllIsCopyTask(idTask);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Error in integration with Database (findAllByIdOriginalTaskAndIsCopy)", result.getMessage());

        verify(repository, times(1)).findAllByIdOriginalTaskAndIsCopy(any(UUID.class));
    }

}
