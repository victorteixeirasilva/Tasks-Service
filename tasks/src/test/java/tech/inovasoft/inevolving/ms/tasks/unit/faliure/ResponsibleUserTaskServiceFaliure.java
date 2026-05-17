package tech.inovasoft.inevolving.ms.tasks.unit.faliure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateResponsibleUserDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.ResponsibleUserTaskService;

import java.sql.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsibleUserTaskServiceFaliure {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ResponsibleUserTaskService responsibleUserTaskService;

    @Test
    void updateResponsibleUser_propagatesNotFoundExceptionFromFindById()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        RequestUpdateResponsibleUserDTO dto =
                new RequestUpdateResponsibleUserDTO(idTask, idUser, UUID.randomUUID());

        when(taskRepository.findById(idUser, idTask))
                .thenThrow(new NotFoundException("(findById) Task not found"));

        // when / then
        assertThrows(NotFoundException.class,
                () -> responsibleUserTaskService.updateResponsibleUser(dto));

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void updateResponsibleUser_propagatesDataBaseExceptionFromFindById()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        RequestUpdateResponsibleUserDTO dto =
                new RequestUpdateResponsibleUserDTO(idTask, idUser, UUID.randomUUID());

        when(taskRepository.findById(idUser, idTask))
                .thenThrow(new DataBaseException("(findById)", null));

        // when / then
        assertThrows(DataBaseException.class,
                () -> responsibleUserTaskService.updateResponsibleUser(dto));

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void updateResponsibleUser_propagatesDataBaseExceptionFromSave()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID idResponsibleUser = UUID.randomUUID();
        RequestUpdateResponsibleUserDTO dto =
                new RequestUpdateResponsibleUserDTO(idTask, idUser, idResponsibleUser);

        Task task = new Task(
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
                null,
                null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class)))
                .thenThrow(new DataBaseException("(save)", null));

        // when / then
        assertThrows(DataBaseException.class,
                () -> responsibleUserTaskService.updateResponsibleUser(dto));

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, times(1)).saveInDataBase(any(Task.class));
    }

    @Test
    void getResponsibleUser_propagatesNotFoundExceptionFromFindById()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();

        when(taskRepository.findById(idUser, idTask))
                .thenThrow(new NotFoundException("(findById) Task not found"));

        // when / then
        assertThrows(NotFoundException.class,
                () -> responsibleUserTaskService.getResponsibleUser(idUser, idTask));

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void getResponsibleUser_propagatesDataBaseExceptionFromFindById()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();

        when(taskRepository.findById(idUser, idTask))
                .thenThrow(new DataBaseException("(findById)", null));

        // when / then
        assertThrows(DataBaseException.class,
                () -> responsibleUserTaskService.getResponsibleUser(idUser, idTask));

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, never()).saveInDataBase(any());
    }
}
