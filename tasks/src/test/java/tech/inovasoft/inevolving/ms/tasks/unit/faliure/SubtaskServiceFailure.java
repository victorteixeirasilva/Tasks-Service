package tech.inovasoft.inevolving.ms.tasks.unit.faliure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import tech.inovasoft.inevolving.ms.tasks.service.SubtaskService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubtaskServiceFailure {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private SubtaskService subtaskService;

    // ==================== 1.3 - Deve falhar ao criar subtarefa sem idParentTask ====================

    /**
     * 1.3 - Deve retornar erro quando idParentTask é null
     */
    @Test
    public void createSubtask_failsWhenIdParentTaskIsNull() throws DataBaseException {
        // Given
        var dto = new RequestSubtaskDTO(
                "Subtask", "Desc",
                LocalDate.of(2025, 5, 12),
                null,
                UUID.randomUUID()
        );

        // When / Then
        assertThrows(NotFoundException.class, () -> subtaskService.createSubtask(dto));
        verifyNoInteractions(taskRepository);
    }

    /**
     * 1.3b - Mensagem de erro deve ser clara quando idParentTask é null
     */
    @Test
    public void createSubtask_errorMessageClearWhenNoParent() throws DataBaseException {
        // Given
        var dto = new RequestSubtaskDTO(
                "Subtask", "Desc",
                LocalDate.of(2025, 5, 12),
                null,
                UUID.randomUUID()
        );

        // When / Then
        NotFoundException ex = assertThrows(NotFoundException.class, () -> subtaskService.createSubtask(dto));
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("idParentTask"));
    }

    /**
     * 1.3c - Nenhuma subtarefa deve ser persistida quando falta idParentTask
     */
    @Test
    public void createSubtask_noSubtaskPersistedWhenNoParent() throws DataBaseException {
        // Given
        var dto = new RequestSubtaskDTO(
                "Subtask", "Desc",
                LocalDate.of(2025, 5, 12),
                null,
                UUID.randomUUID()
        );

        // When / Then
        assertThrows(NotFoundException.class, () -> subtaskService.createSubtask(dto));
        verify(taskRepository, never()).saveInDataBase(any(Task.class));
    }

    // ==================== 1.3 / 4.3 - Deve falhar quando tarefa pai não existe ====================

    /**
     * 4.3 - Deve retornar erro quando idParentTask não corresponde a nenhuma tarefa
     */
    @Test
    public void createSubtask_failsWhenParentTaskNotFound()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();

        var dto = new RequestSubtaskDTO(
                "Subtask", "Desc",
                LocalDate.of(2025, 5, 12),
                idParentTask,
                idUser
        );

        when(taskRepository.findById(idUser, idParentTask))
                .thenThrow(new NotFoundException("(findById) Task not found"));

        // When / Then
        assertThrows(NotFoundException.class, () -> subtaskService.createSubtask(dto));
        verify(taskRepository, never()).saveInDataBase(any(Task.class));
    }

    /**
     * 4.3 - getSubtasks deve falhar quando idParentTask não existe
     */
    @Test
    public void getSubtasks_failsWhenParentTaskNotFound()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();

        when(taskRepository.findById(idUser, idParentTask))
                .thenThrow(new NotFoundException("(findById) Task not found"));

        // When / Then
        assertThrows(NotFoundException.class, () -> subtaskService.getSubtasks(idUser, idParentTask));
    }

    // ==================== 5.1 - promoteToParent deve falhar quando tarefa não existe ====================

    /**
     * 5.1 - promoteToParent deve falhar quando a tarefa não existe
     */
    @Test
    public void promoteToParent_failsWhenTaskNotFound()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();

        when(taskRepository.findById(idUser, idTask))
                .thenThrow(new NotFoundException("(findById) Task not found"));

        // When / Then
        assertThrows(NotFoundException.class, () -> subtaskService.promoteToParent(idUser, idTask));

        verify(taskRepository, never()).saveInDataBase(any(Task.class));
    }

    // ==================== 6.4 - Deve falhar ao tentar remover subtarefa inexistente ====================

    /**
     * 6.4 - deleteSubtask deve falhar quando id não existe
     */
    @Test
    public void deleteSubtask_failsWhenSubtaskNotFound()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();

        when(taskRepository.findById(idUser, idTask))
                .thenThrow(new NotFoundException("(findById) Task not found"));

        // When / Then
        assertThrows(NotFoundException.class, () -> subtaskService.deleteSubtask(idUser, idTask));

        verify(taskRepository, never()).deleteTask(any(Task.class));
    }

    /**
     * 6.4 - Nenhuma tarefa pai deve ser modificada quando subtarefa inexistente
     */
    @Test
    public void deleteSubtask_noParentModifiedWhenSubtaskNotFound()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();

        when(taskRepository.findById(idUser, idTask))
                .thenThrow(new NotFoundException("(findById) Task not found"));

        // When / Then
        assertThrows(NotFoundException.class, () -> subtaskService.deleteSubtask(idUser, idTask));

        verify(taskRepository, never()).saveInDataBase(any(Task.class));
        verify(taskRepository, never()).findAllByIdParentTask(any(UUID.class));
    }

    // ==================== 9.4 - Validar limite de profundidade ====================

    /**
     * 9.4 - Subtarefa não deve ter idParentTask null após criação (deve ter profundidade 1)
     */
    @Test
    public void createSubtask_subtaskHasParentTaskId()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, false, false, false, null, null
        );

        var dto = new RequestSubtaskDTO("Sub", "Desc", LocalDate.of(2025, 5, 12), idParentTask, idUser);

        var savedSubtask = new Task(
                UUID.randomUUID(), "Sub", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(savedSubtask);

        // When
        ResponseSubtaskDTO result = subtaskService.createSubtask(dto);

        // Then - subtask always has idParentTask set
        assertNotNull(result.idParentTask());
        assertEquals(idParentTask, result.idParentTask());
    }
}
