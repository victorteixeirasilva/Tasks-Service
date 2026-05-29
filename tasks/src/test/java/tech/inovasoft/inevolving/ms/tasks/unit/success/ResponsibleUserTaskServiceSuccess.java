package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateResponsibleUserDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseResponsibleUserDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.ResponsibleUserTaskService;

import java.sql.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsibleUserTaskServiceSuccess {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ResponsibleUserTaskService responsibleUserTaskService;

    @Test
    void updateResponsibleUser_setsResponsibleUserAndReturnsResponse()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID idResponsibleUser = UUID.randomUUID();

        Task task = new Task(idTask, "Name Task", "Description Task", Status.TODO, Date.valueOf("2025-05-12"), null, idUser, null, null, false, false, false, null, null, null, null, null, null);

        RequestUpdateResponsibleUserDTO dto =
                new RequestUpdateResponsibleUserDTO(idTask, idUser, idResponsibleUser);

        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ResponseResponsibleUserDTO result = responsibleUserTaskService.updateResponsibleUser(dto);

        // then
        assertNotNull(result);
        assertEquals(idTask, result.idTask());
        assertEquals(idResponsibleUser, result.idResponsibleUser());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, times(1)).saveInDataBase(captor.capture());
        assertEquals(idResponsibleUser, captor.getValue().getIdResponsibleUser());
    }

    @Test
    void updateResponsibleUser_withNullResponsibleUser_savesNullAndReturnsResponse()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID existingResponsibleUser = UUID.randomUUID();

        Task task = new Task(idTask, "Name Task", "Description Task", Status.TODO, Date.valueOf("2025-05-12"), null, idUser, null, null, false, false, false, null, existingResponsibleUser, null, null, null, null);

        RequestUpdateResponsibleUserDTO dto =
                new RequestUpdateResponsibleUserDTO(idTask, idUser, null);

        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ResponseResponsibleUserDTO result = responsibleUserTaskService.updateResponsibleUser(dto);

        // then
        assertNotNull(result);
        assertEquals(idTask, result.idTask());
        assertNull(result.idResponsibleUser());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).saveInDataBase(captor.capture());
        assertNull(captor.getValue().getIdResponsibleUser());
    }

    @Test
    void getResponsibleUser_returnsResponseWithResponsibleUser()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID idResponsibleUser = UUID.randomUUID();

        Task task = new Task(idTask, "Name Task", "Description Task", Status.TODO, Date.valueOf("2025-05-12"), null, idUser, null, null, false, false, false, null, idResponsibleUser, null, null, null, null);

        when(taskRepository.findById(idUser, idTask)).thenReturn(task);

        // when
        ResponseResponsibleUserDTO result =
                responsibleUserTaskService.getResponsibleUser(idUser, idTask);

        // then
        assertNotNull(result);
        assertEquals(idTask, result.idTask());
        assertEquals(idResponsibleUser, result.idResponsibleUser());

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, never()).saveInDataBase(any());
    }

    @Test
    void getResponsibleUser_withNullResponsibleUser_returnsNullWithoutException()
            throws DataBaseException, NotFoundException {
        // given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();

        Task task = new Task(idTask, "Name Task", "Description Task", Status.TODO, Date.valueOf("2025-05-12"), null, idUser, null, null, false, false, false, null, null, null, null, null, null);

        when(taskRepository.findById(idUser, idTask)).thenReturn(task);

        // when
        ResponseResponsibleUserDTO result =
                responsibleUserTaskService.getResponsibleUser(idUser, idTask);

        // then
        assertNotNull(result);
        assertEquals(idTask, result.idTask());
        assertNull(result.idResponsibleUser());

        verify(taskRepository, times(1)).findById(idUser, idTask);
        verify(taskRepository, never()).saveInDataBase(any());
    }
}
