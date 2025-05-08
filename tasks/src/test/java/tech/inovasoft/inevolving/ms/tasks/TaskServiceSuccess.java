package tech.inovasoft.inevolving.ms.tasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;


import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceSuccess {
    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    // Given (Dado)
    // When (Quando)
    // Then (Então)

    @Test
    public void addTaskWithObjective() {
        // Given (Dado)
        var idUser = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var taskDTO = new RequestTaskDTO(
                "nameTask",
                "descriptionTask",
                Date.valueOf("2025-01-01"),
                Optional.of(idObjective),
                idUser
        );
        var expectedTask = new Task(
            UUID.randomUUID(),
            taskDTO.nameTask(),
            taskDTO.descriptionTask(),
            Status.TODO,
            taskDTO.dateTask(),
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
        when(repository.save(any())).thenReturn(expectedTask);
        var result = service.addTask(taskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTask, result);

        verify(repository, times(1)).save(any());
    }

}
