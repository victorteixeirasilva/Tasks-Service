package tech.inovasoft.inevolving.ms.tasks.unit.faliure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateDateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.DateTaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DateTaskServiceFaliure {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DateTaskService dateTaskService;

    @Test
    void updateDateTask_propagatesNotFoundExceptionFromFindById() throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        RequestUpdateDateTaskDTO dto = new RequestUpdateDateTaskDTO(LocalDate.of(2026, 4, 15), idTask, idUser);

        when(taskRepository.findById(idTask, idUser)).thenThrow(new NotFoundException("(findById) Task not found"));

        assertThrows(NotFoundException.class, () -> dateTaskService.updateDateTask(dto));

        verify(taskRepository, times(1)).findById(idTask, idUser);
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void updateDateTask_propagatesUserWithoutAuthorizationFromFindById()
            throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        RequestUpdateDateTaskDTO dto = new RequestUpdateDateTaskDTO(LocalDate.of(2026, 4, 15), idTask, idUser);

        when(taskRepository.findById(idTask, idUser)).thenThrow(new UserWithoutAuthorizationAboutTheTaskException());

        assertThrows(UserWithoutAuthorizationAboutTheTaskException.class, () -> dateTaskService.updateDateTask(dto));

        verify(taskRepository, times(1)).findById(idTask, idUser);
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void updateDateTask_propagatesDataBaseExceptionFromFindById()
            throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        RequestUpdateDateTaskDTO dto = new RequestUpdateDateTaskDTO(LocalDate.of(2026, 4, 15), idTask, idUser);

        when(taskRepository.findById(idTask, idUser)).thenThrow(new DataBaseException("(findById)", null));

        assertThrows(DataBaseException.class, () -> dateTaskService.updateDateTask(dto));

        verify(taskRepository, times(1)).findById(idTask, idUser);
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void updateDateTask_propagatesDataBaseExceptionFromSave() throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        RequestUpdateDateTaskDTO dto = new RequestUpdateDateTaskDTO(LocalDate.of(2026, 4, 15), idTask, idUser);

        Task task = new Task(
                idTask,
                "Name",
                "Desc",
                Status.TODO,
                Date.valueOf("2025-01-01"),
                null,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );

        when(taskRepository.findById(idTask, idUser)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenThrow(new DataBaseException("(save)", null));

        assertThrows(DataBaseException.class, () -> dateTaskService.updateDateTask(dto));

        verify(taskRepository, times(1)).findById(idTask, idUser);
        verify(taskRepository, times(1)).saveInDataBase(any(Task.class));
    }
}
