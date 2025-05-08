package tech.inovasoft.inevolving.ms.tasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceSuccess {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    // Given (Dado)
    // When (Quando)
    // Then (Então)

    @Test
    public void addTaskWithObjective() throws DataBaseException {
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

        Task expectedTask = new Task(
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
        when(repository.save(any(Task.class))).thenReturn(expectedTask);
        var result = service.addTask(taskDTO);

        // Then (Então)
        assertNotNull(result);
        assertEquals(expectedTask.getId(), result.getId());
        assertEquals(expectedTask.getNameTask(), result.getNameTask());
        assertEquals(expectedTask.getDescriptionTask(), result.getDescriptionTask());
        assertEquals(expectedTask.getStatus(), result.getStatus());
        assertEquals(expectedTask.getDateTask(), result.getDateTask());
        assertEquals(expectedTask.getIdObjective(), result.getIdObjective());
        assertEquals(expectedTask.getIdUser(), result.getIdUser());

        verify(repository, times(1)).save(any());
    }

}
