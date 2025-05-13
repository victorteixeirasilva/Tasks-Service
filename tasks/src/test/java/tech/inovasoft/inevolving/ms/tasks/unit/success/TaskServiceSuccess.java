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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceSuccess {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SimpleTaskService simpleTaskService;

    @InjectMocks
    private TaskService service;


    @Test
    public void lockTaskByObjective() throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        List<Task> tasks = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 5, 13);
        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(
                    UUID.randomUUID(),
                    "Task " + i,
                    "Description " + i,
                    Status.TODO,
                    Date.valueOf(currentDate),
                    idObjective,
                    idUser,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null
            ));
            currentDate = currentDate.plusDays(1);
        }

        // When (Quando)
        when(taskRepository.findAllByIdObjective(idObjective)).thenReturn(tasks);
        when(simpleTaskService.deleteTask(any(UUID.class), any(UUID.class))).thenReturn(new ResponseMessageDTO(""));
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(new Task());
        var result = service.lockTaskByObjective(idUser, idObjective, Date.valueOf("2025-05-17"));

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks locked!", result.message());

        verify(taskRepository, times(1)).findAllByIdObjective(idObjective);
        verify(simpleTaskService, times(5)).deleteTask(any(UUID.class), any(UUID.class));
        verify(taskRepository, times(5)).saveInDataBase(any(Task.class));
    }
}
