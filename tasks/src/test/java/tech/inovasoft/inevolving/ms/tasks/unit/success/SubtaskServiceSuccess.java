package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.SubtaskService;
import tech.inovasoft.inevolving.ms.tasks.service.client.ObjectivesServiceClient;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
public class SubtaskServiceSuccess {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private SubtaskService subtaskService;

    @Mock
    private ObjectivesServiceClient objectivesServiceClient;

    @InjectMocks
    private SimpleTaskService simpleTaskService;

    // ==================== 1. Testes de Criação de Subtarefa ====================

    /**
     * 1.1 - Deve criar uma subtarefa com sucesso
     */
    @Test
    public void createSubtask_success()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var parentTask = new Task(
                idParentTask, "Parent Task", "Parent Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, false, false, false, null, null
        );

        var dto = new RequestSubtaskDTO(
                "Subtask Name", "Subtask Desc",
                LocalDate.of(2025, 5, 12),
                idParentTask, idUser
        );

        var savedSubtask = new Task(
                UUID.randomUUID(), dto.nameTask(), dto.descriptionTask(),
                Status.TODO, Date.valueOf(dto.dateTask()),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(savedSubtask);

        // When
        ResponseSubtaskDTO result = subtaskService.createSubtask(dto);

        // Then
        assertNotNull(result);
        assertEquals(savedSubtask.getId(), result.id());
        assertEquals(dto.nameTask(), result.nameTask());
        assertEquals(dto.descriptionTask(), result.descriptionTask());
        assertEquals(Status.TODO, result.status());
        assertEquals(idObjective, result.idObjective());
        assertEquals(idUser, result.idUser());
        assertEquals(idParentTask, result.idParentTask());

        verify(taskRepository, times(1)).findById(idUser, idParentTask);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, atLeast(1)).saveInDataBase(captor.capture());
        assertEquals(idUser, captor.getAllValues().get(0).getIdResponsibleUser());
    }

    /**
     * 1.2 - Deve atualizar hasSubtasks da tarefa pai para true quando a primeira subtarefa é criada
     */
    @Test
    public void createSubtask_updatesParentHasSubtasksToTrue()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var parentTask = new Task(
                idParentTask, "Parent Task", "Parent Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, false, false, false, null, null
        );

        var dto = new RequestSubtaskDTO(
                "Subtask", "Desc",
                LocalDate.of(2025, 5, 12),
                idParentTask, idUser
        );

        var savedSubtask = new Task(
                UUID.randomUUID(), dto.nameTask(), dto.descriptionTask(),
                Status.TODO, Date.valueOf(dto.dateTask()),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(savedSubtask);

        // When
        subtaskService.createSubtask(dto);

        // Then - parent's hasSubtasks must be set to true and saved
        verify(taskRepository, atLeast(2)).saveInDataBase(any(Task.class));
        assertTrue(parentTask.getHasSubtasks());
    }

    /**
     * 1.2b - Se hasSubtasks já era true, deve permanecer true sem salvar novamente
     */
    @Test
    public void createSubtask_parentAlreadyHasSubtasks_remainsTrue()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var parentTask = new Task(
                idParentTask, "Parent Task", "Parent Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        var dto = new RequestSubtaskDTO(
                "Subtask", "Desc",
                LocalDate.of(2025, 5, 12),
                idParentTask, idUser
        );

        var savedSubtask = new Task(
                UUID.randomUUID(), dto.nameTask(), dto.descriptionTask(),
                Status.TODO, Date.valueOf(dto.dateTask()),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(savedSubtask);

        // When
        subtaskService.createSubtask(dto);

        // Then - only the subtask is saved (parent not saved again since already true)
        verify(taskRepository, times(1)).saveInDataBase(any(Task.class));
        assertTrue(parentTask.getHasSubtasks());
    }

    /**
     * 1.5 - Deve herdar automaticamente o objetivo da tarefa pai
     */
    @Test
    public void createSubtask_inheritsObjectiveFromParent()
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

        var dto = new RequestSubtaskDTO(
                "Subtask", "Desc",
                LocalDate.of(2025, 5, 12),
                idParentTask, idUser
        );

        var savedSubtask = new Task(
                UUID.randomUUID(), dto.nameTask(), dto.descriptionTask(),
                Status.TODO, Date.valueOf(dto.dateTask()),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(savedSubtask);

        // When
        ResponseSubtaskDTO result = subtaskService.createSubtask(dto);

        // Then - subtask must inherit parent's objective
        assertEquals(idObjective, result.idObjective());
    }

    // ==================== 2. Atualização de Objetivo da Tarefa Pai ====================

    /**
     * 2.1 - Deve atualizar objetivo de todas as subtarefas quando o objetivo da tarefa pai mudar
     */
    @Test
    public void updateTask_cascadesObjectiveToAllSubtasks()
            throws DataBaseException, NotFoundException,
            ExecutionException, InterruptedException, TimeoutException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idOldObjective = UUID.randomUUID();
        var idNewObjective = UUID.randomUUID();

        var parentTask = new Task(
                idTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idOldObjective, idUser,
                null, null, true, false, false, null, null
        );

        var subtask1 = new Task(
                UUID.randomUUID(), "Sub1", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idOldObjective, idUser,
                idTask, null, false, false, false, null, null
        );
        var subtask2 = new Task(
                UUID.randomUUID(), "Sub2", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idOldObjective, idUser,
                idTask, null, false, false, false, null, null
        );

        var updateDTO = new RequestUpdateTaskDTO("Parent Updated", "Desc", idNewObjective);

        when(taskRepository.findById(idUser, idTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(parentTask);
        when(taskRepository.findAllByIdParentTask(idTask)).thenReturn(Arrays.asList(subtask1, subtask2));

        // When
        ResponseTaskDTO result = simpleTaskService.updateTask(idUser, idTask, updateDTO);

        // Then - parent saved + both subtasks updated
        verify(taskRepository, times(3)).saveInDataBase(any(Task.class));
        assertEquals(idNewObjective, subtask1.getIdObjective());
        assertEquals(idNewObjective, subtask2.getIdObjective());
    }

    /**
     * 2.2 - Deve atualizar apenas subtarefas diretas da tarefa pai
     */
    @Test
    public void updateTask_updatesOnlyDirectSubtasks()
            throws DataBaseException, NotFoundException,
            ExecutionException, InterruptedException, TimeoutException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var otherParentId = UUID.randomUUID();
        var idOldObjective = UUID.randomUUID();
        var idNewObjective = UUID.randomUUID();

        var parentTask = new Task(
                idTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idOldObjective, idUser,
                null, null, true, false, false, null, null
        );

        var directSubtask = new Task(
                UUID.randomUUID(), "DirectSub", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idOldObjective, idUser,
                idTask, null, false, false, false, null, null
        );

        var updateDTO = new RequestUpdateTaskDTO("Parent Updated", "Desc", idNewObjective);

        when(taskRepository.findById(idUser, idTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(parentTask);
        when(taskRepository.findAllByIdParentTask(idTask)).thenReturn(Collections.singletonList(directSubtask));

        // When
        simpleTaskService.updateTask(idUser, idTask, updateDTO);

        // Then - only findAllByIdParentTask for the target task is called
        verify(taskRepository, times(1)).findAllByIdParentTask(idTask);
        verify(taskRepository, never()).findAllByIdParentTask(otherParentId);
        assertEquals(idNewObjective, directSubtask.getIdObjective());
    }

    /**
     * 2.3 - Deve funcionar mesmo sem subtarefas (hasSubtasks false)
     */
    @Test
    public void updateTask_noSubtasks_noError()
            throws DataBaseException, NotFoundException,
            ExecutionException, InterruptedException, TimeoutException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idOldObjective = UUID.randomUUID();
        var idNewObjective = UUID.randomUUID();

        var task = new Task(
                idTask, "Task", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idOldObjective, idUser,
                null, null, false, false, false, null, null
        );

        var updateDTO = new RequestUpdateTaskDTO("Task Updated", "Desc", idNewObjective);

        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(task);

        // When
        ResponseTaskDTO result = simpleTaskService.updateTask(idUser, idTask, updateDTO);

        // Then - no subtask update queries
        assertNotNull(result);
        verify(taskRepository, never()).findAllByIdParentTask(any(UUID.class));
    }

    // ==================== 4. Testes do Novo Endpoint de Subtarefas ====================

    /**
     * 4.1 - Deve retornar todas as subtarefas de uma tarefa pai
     */
    @Test
    public void getSubtasks_returnsAllSubtasks()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        var sub1 = new Task(
                UUID.randomUUID(), "Sub1", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );
        var sub2 = new Task(
                UUID.randomUUID(), "Sub2", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Arrays.asList(sub1, sub2));

        // When
        List<Task> result = subtaskService.getSubtasks(idUser, idParentTask);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> idParentTask.equals(t.getIdParentTask())));

        verify(taskRepository, times(1)).findById(idUser, idParentTask);
        verify(taskRepository, times(1)).findAllByIdParentTask(idParentTask);
    }

    /**
     * 4.2 - Deve retornar lista vazia se não houver subtarefas (HTTP 200, não 404)
     */
    @Test
    public void getSubtasks_returnsEmptyListWhenNoSubtasks()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                UUID.randomUUID(), idUser,
                null, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Collections.emptyList());

        // When
        List<Task> result = subtaskService.getSubtasks(idUser, idParentTask);

        // Then - empty list, no exception
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * 4.5 - Deve retornar subtarefas com todas as informações necessárias
     */
    @Test
    public void getSubtasks_returnsFullSubtaskData()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();
        var subtaskId = UUID.randomUUID();

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        var subtask = new Task(
                subtaskId, "Sub Name", "Sub Desc",
                Status.IN_PROGRESS, Date.valueOf("2025-06-01"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Collections.singletonList(subtask));

        // When
        List<Task> result = subtaskService.getSubtasks(idUser, idParentTask);

        // Then - all fields present
        assertNotNull(result);
        assertEquals(1, result.size());
        Task returned = result.get(0);
        assertEquals(subtaskId, returned.getId());
        assertEquals("Sub Name", returned.getNameTask());
        assertEquals("Sub Desc", returned.getDescriptionTask());
        assertEquals(Status.IN_PROGRESS, returned.getStatus());
        assertEquals(idObjective, returned.getIdObjective());
        assertEquals(idUser, returned.getIdUser());
        assertEquals(idParentTask, returned.getIdParentTask());
    }

    // ==================== 5. Testes de Transformação de Subtarefa em Tarefa Pai ====================

    /**
     * 5.1 - Deve transformar uma subtarefa em tarefa pai
     */
    @Test
    public void promoteToParent_success()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var subtask = new Task(
                idTask, "Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        var promotedTask = new Task(
                idTask, "Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, false, false, false, null, null
        );

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(subtask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(promotedTask);
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Collections.emptyList());
        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);

        // When
        ResponseSubtaskDTO result = subtaskService.promoteToParent(idUser, idTask);

        // Then
        assertNotNull(result);
        assertNull(result.idParentTask());
        verify(taskRepository, atLeast(1)).saveInDataBase(any(Task.class));
    }

    /**
     * 5.2 - Deve manter o objetivo ao transformar em tarefa pai
     */
    @Test
    public void promoteToParent_maintainsObjective()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var subtask = new Task(
                idTask, "Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        var promotedTask = new Task(
                idTask, "Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, false, false, false, null, null
        );

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(subtask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(promotedTask);
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Collections.emptyList());
        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);

        // When
        ResponseSubtaskDTO result = subtaskService.promoteToParent(idUser, idTask);

        // Then - objective unchanged
        assertEquals(idObjective, result.idObjective());
    }

    /**
     * 5.4 - Deve atualizar hasSubtasks da tarefa pai anterior quando promovida a tarefa pai
     */
    @Test
    public void promoteToParent_updatesOldParentHasSubtasksToFalse()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var subtask = new Task(
                idTask, "Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        var promotedTask = new Task(
                idTask, "Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, false, false, false, null, null
        );

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(subtask);
        when(taskRepository.saveInDataBase(any(Task.class))).thenReturn(promotedTask);
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Collections.emptyList());
        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);

        // When
        subtaskService.promoteToParent(idUser, idTask);

        // Then - old parent's hasSubtasks set to false
        assertFalse(parentTask.getHasSubtasks());
        verify(taskRepository, atLeast(2)).saveInDataBase(any(Task.class));
    }

    /**
     * 5.5 - Deve ser idempotente (promover task que já é pai não causa erro)
     */
    @Test
    public void promoteToParent_isIdempotent()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var task = new Task(
                idTask, "Task", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(task);

        // When - task already has no parent (already a parent task)
        ResponseSubtaskDTO result = subtaskService.promoteToParent(idUser, idTask);

        // Then - no error, no save needed
        assertNotNull(result);
        assertNull(result.idParentTask());
        verify(taskRepository, never()).saveInDataBase(any(Task.class));
    }

    // ==================== 6. Testes de Remoção de Subtarefa ====================

    /**
     * 6.1 - Deve remover uma subtarefa com sucesso
     */
    @Test
    public void deleteSubtask_success()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var subtask = new Task(
                idTask, "Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(subtask);
        when(taskRepository.deleteTask(subtask)).thenReturn(new ResponseMessageDTO("Successfully delete task"));
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Collections.emptyList());
        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(parentTask)).thenReturn(parentTask);

        // When
        ResponseMessageDTO result = subtaskService.deleteSubtask(idUser, idTask);

        // Then
        assertNotNull(result);
        assertEquals("Successfully delete task", result.message());
        verify(taskRepository, times(1)).deleteTask(subtask);
    }

    /**
     * 6.2 - hasSubtasks permanece true quando ainda existem outras subtarefas
     */
    @Test
    public void deleteSubtask_keepsParentHasSubtasksTrueWhenOthersExist()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var subtask = new Task(
                idTask, "Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        var anotherSubtask = new Task(
                UUID.randomUUID(), "Another Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(subtask);
        when(taskRepository.deleteTask(subtask)).thenReturn(new ResponseMessageDTO("Successfully delete task"));
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Collections.singletonList(anotherSubtask));

        // When
        subtaskService.deleteSubtask(idUser, idTask);

        // Then - parent not saved (still has subtasks)
        assertTrue(parentTask.getHasSubtasks());
        verify(taskRepository, never()).saveInDataBase(any(Task.class));
    }

    /**
     * 6.3 - Deve atualizar hasSubtasks para false quando a última subtarefa é removida
     */
    @Test
    public void deleteSubtask_setsParentHasSubtasksFalse_whenLastSubtaskRemoved()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idParentTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var subtask = new Task(
                idTask, "Last Subtask", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idParentTask, null, false, false, false, null, null
        );

        var parentTask = new Task(
                idParentTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(subtask);
        when(taskRepository.deleteTask(subtask)).thenReturn(new ResponseMessageDTO("Successfully delete task"));
        when(taskRepository.findAllByIdParentTask(idParentTask)).thenReturn(Collections.emptyList());
        when(taskRepository.findById(idUser, idParentTask)).thenReturn(parentTask);
        when(taskRepository.saveInDataBase(parentTask)).thenReturn(parentTask);

        // When
        subtaskService.deleteSubtask(idUser, idTask);

        // Then - parent's hasSubtasks set to false
        assertFalse(parentTask.getHasSubtasks());
        verify(taskRepository, times(1)).saveInDataBase(parentTask);
    }

    // ==================== 7. Testes de Remoção de Tarefa Pai com Subtarefas ====================

    /**
     * 7.1 - Deve remover todas as subtarefas antes de remover a tarefa pai
     */
    @Test
    public void deleteParentTask_deletesAllSubtasksFirst()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();
        var idObjective = UUID.randomUUID();

        var parentTask = new Task(
                idTask, "Parent", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                null, null, true, false, false, null, null
        );

        var sub1 = new Task(
                UUID.randomUUID(), "Sub1", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idTask, null, false, false, false, null, null
        );
        var sub2 = new Task(
                UUID.randomUUID(), "Sub2", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                idObjective, idUser,
                idTask, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(parentTask);
        when(taskRepository.findAllByIdParentTask(idTask)).thenReturn(Arrays.asList(sub1, sub2));
        when(taskRepository.deleteTask(any(Task.class))).thenReturn(new ResponseMessageDTO("Successfully delete task"));

        // When
        ResponseMessageDTO result = simpleTaskService.deleteTask(idUser, idTask);

        // Then - subtasks deleted first, then parent
        assertNotNull(result);
        verify(taskRepository, times(3)).deleteTask(any(Task.class));
        verify(taskRepository, times(1)).findAllByIdParentTask(idTask);
    }

    /**
     * 7.5 - Deve remover tarefa pai sem subtarefas normalmente
     */
    @Test
    public void deleteParentTask_noSubtasks_deletesNormally()
            throws DataBaseException, NotFoundException {
        // Given
        var idUser = UUID.randomUUID();
        var idTask = UUID.randomUUID();

        var task = new Task(
                idTask, "Task", "Desc",
                Status.TODO, Date.valueOf("2025-05-12"),
                UUID.randomUUID(), idUser,
                null, null, false, false, false, null, null
        );

        when(taskRepository.findById(idUser, idTask)).thenReturn(task);
        when(taskRepository.deleteTask(task)).thenReturn(new ResponseMessageDTO("Successfully delete task"));

        // When
        ResponseMessageDTO result = simpleTaskService.deleteTask(idUser, idTask);

        // Then
        assertNotNull(result);
        assertEquals("Successfully delete task", result.message());
        verify(taskRepository, never()).findAllByIdParentTask(any(UUID.class));
        verify(taskRepository, times(1)).deleteTask(task);
    }

    // ==================== 8. Testes de Consistência de Dados ====================

    /**
     * 8.3 - idObjective da subtarefa sempre coincide com a tarefa pai
     */
    @Test
    public void createSubtask_objectiveMatchesParent()
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

        // Then
        assertEquals(parentTask.getIdObjective(), result.idObjective());
    }
}
