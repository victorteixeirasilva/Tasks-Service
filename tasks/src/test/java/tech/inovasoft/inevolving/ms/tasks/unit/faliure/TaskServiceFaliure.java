package tech.inovasoft.inevolving.ms.tasks.unit.faliure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundTasksInDateRangeException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceFaliure {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SimpleTaskService simpleTaskService;

    @InjectMocks
    private TaskService service;

    @Test
    public void lockTaskByObjectiveUserWithoutAuthorizationAboutTheTaskException() throws DataBaseException {
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
                    UUID.randomUUID(),
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
        var result =  assertThrows(UserWithoutAuthorizationAboutTheTaskException.class, () -> {
            service.lockTaskByObjective(idUser, idObjective, Date.valueOf("2025-05-17"));
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("User without authorization about the task.", result.getMessage());

        verify(taskRepository, times(1)).findAllByIdObjective(idObjective);
    }

    @Test
    public void getTasksInDateRangeNotFoundTasksInDateRangeException() throws NotFoundTasksInDateRangeException {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        Date startDate = Date.valueOf("2025-05-01");
        Date endDate = Date.valueOf("2025-05-31");
        List<Task> tasks = new ArrayList<>();


        // When (Quando)
        when(taskRepository.findAllByIdUserAndDateRange(idUser, startDate, endDate)).thenReturn(tasks);
        var result = assertThrows(NotFoundTasksInDateRangeException.class, () -> {
            service.getTasksInDateRange(idUser, startDate, endDate);
        });

        // Then (Então)
        assertNotNull(result);
        assertEquals("Tasks not found in date range", result.getMessage());

        verify(taskRepository, times(1)).findAllByIdUserAndDateRange(idUser, startDate, endDate);
    }
}
