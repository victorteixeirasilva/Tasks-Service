package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateDateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DateTaskServiceSuccess {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DateTaskService dateTaskService;

    @Test
    void updateDateTask_setsNewDateAndReturnsResponse()
            throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        LocalDate newLocalDate = LocalDate.of(2026, 4, 15);
        Date previousSqlDate = Date.valueOf("2025-05-12");

        Task task = new Task(
                idTask,
                "Name Task",
                "Description Task",
                Status.TODO,
                previousSqlDate,
                null,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null
        );

        RequestUpdateDateTaskDTO dto = new RequestUpdateDateTaskDTO(newLocalDate, idTask, idUser);

        when(taskRepository.findById(idTask, idUser)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseTaskDTO result = dateTaskService.updateDateTask(dto);

        assertNotNull(result);
        assertEquals(Date.valueOf(newLocalDate), result.dateTask());
        assertEquals(idTask, result.id());
        assertEquals("Name Task", result.nameTask());

        ArgumentCaptor<Task> savedCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).findById(idTask, idUser);
        verify(taskRepository, times(1)).saveInDataBase(savedCaptor.capture());
        assertEquals(Date.valueOf(newLocalDate), savedCaptor.getValue().getDateTask());
    }
}
