package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubtaskServiceTimestampsSuccess {

    private static final ZoneId USER_ZONE = ZoneId.of("America/Sao_Paulo");

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private tech.inovasoft.inevolving.ms.tasks.service.SubtaskService subtaskService;

    @Test
    void createSubtask_setsCreatedAtOnSavedSubtask() throws DataBaseException, NotFoundException {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idParent = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        Task parent = new Task(
                idParent, "Parent", "Desc", Status.TODO, Date.valueOf("2026-05-29"),
                idObjective, idUser, null, null, false, false, false, null, null,
                null, null, null, null
        );
        RequestSubtaskDTO dto = new RequestSubtaskDTO(
                "Sub", "Sub desc", LocalDate.of(2026, 5, 29), idParent, idUser
        );
        when(repository.findById(idUser, idParent)).thenReturn(parent);
        when(repository.saveInDataBase(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        // When
        ResponseSubtaskDTO result = subtaskService.createSubtask(dto, USER_ZONE);

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository, atLeastOnce()).saveInDataBase(captor.capture());
        Task savedSubtask = captor.getAllValues().stream()
                .filter(t -> t.getIdParentTask() != null)
                .findFirst()
                .orElseThrow();
        assertNotNull(savedSubtask.getCreatedAt());
        assertNotNull(result.createdAt());
    }
}
