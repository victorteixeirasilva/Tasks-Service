package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.domain.util.TaskTimestampHelper;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.sql.Date;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class SubtaskService {

    @Autowired
    private TaskRepository repository;

    /**
     * @description - Creates a new subtask, inheriting the objective from the parent task and setting
     *               hasSubtasks=true on the parent. | Cria uma nova subtarefa, herdando o objetivo da
     *               tarefa pai e definindo hasSubtasks=true na tarefa pai.
     */
    public ResponseSubtaskDTO createSubtask(RequestSubtaskDTO dto, ZoneId userZone)
            throws DataBaseException, NotFoundException {

        if (dto.idParentTask() == null) {
            throw new NotFoundException("idParentTask is required to create a subtask");
        }

        Task parentTask = repository.findById(dto.idUser(), dto.idParentTask());

        Task subtask = new Task();
        subtask.setNameTask(dto.nameTask());
        subtask.setDescriptionTask(dto.descriptionTask());
        subtask.setStatus(Status.TODO);
        subtask.setDateTask(Date.valueOf(dto.dateTask()));
        subtask.setIdObjective(parentTask.getIdObjective());
        subtask.setIdUser(dto.idUser());
        subtask.setIdResponsibleUser(dto.idUser());
        subtask.setIdParentTask(dto.idParentTask());
        subtask.setHasSubtasks(false);
        subtask.setIsCopy(false);
        subtask.setBlockedByObjective(false);
        TaskTimestampHelper.applyOnCreate(subtask);

        Task savedSubtask = repository.saveInDataBase(subtask);

        if (!Boolean.TRUE.equals(parentTask.getHasSubtasks())) {
            parentTask.setHasSubtasks(true);
            repository.saveInDataBase(parentTask);
        }

        return new ResponseSubtaskDTO(savedSubtask, userZone);
    }

    /**
     * @description - Returns all subtasks of a given parent task. Returns empty list if none exist.
     *               | Retorna todas as subtarefas de uma tarefa pai. Retorna lista vazia se não houver.
     */
    public List<Task> getSubtasks(UUID idUser, UUID idParentTask)
            throws DataBaseException, NotFoundException {

        repository.findById(idUser, idParentTask);
        return repository.findAllByIdParentTask(idParentTask);
    }

    /**
     * @description - Promotes a subtask to a parent task by clearing idParentTask.
     *               Updates previous parent's hasSubtasks if needed.
     *               | Promove uma subtarefa para tarefa pai limpando o idParentTask.
     *               Atualiza o hasSubtasks da tarefa pai anterior se necessário.
     */
    public ResponseSubtaskDTO promoteToParent(UUID idUser, UUID idTask, ZoneId userZone)
            throws DataBaseException, NotFoundException {

        Task task = repository.findById(idUser, idTask);

        UUID previousParentId = task.getIdParentTask();
        if (previousParentId == null) {
            return new ResponseSubtaskDTO(task, userZone);
        }

        task.setIdParentTask(null);
        Task savedTask = repository.saveInDataBase(task);

        updateParentHasSubtasks(idUser, previousParentId);

        return new ResponseSubtaskDTO(savedTask, userZone);
    }

    /**
     * @description - Deletes a subtask and updates the parent task's hasSubtasks if it was the last one.
     *               | Deleta uma subtarefa e atualiza o hasSubtasks da tarefa pai caso seja a última.
     */
    public ResponseMessageDTO deleteSubtask(UUID idUser, UUID idTask)
            throws DataBaseException, NotFoundException {

        Task subtask = repository.findById(idUser, idTask);
        UUID parentId = subtask.getIdParentTask();

        ResponseMessageDTO result = repository.deleteTask(subtask);

        if (parentId != null) {
            updateParentHasSubtasks(idUser, parentId);
        }

        return result;
    }

    /**
     * @description - Checks if the parent task still has subtasks; if not, sets hasSubtasks=false.
     *               | Verifica se a tarefa pai ainda possui subtarefas; se não, define hasSubtasks=false.
     */
    private void updateParentHasSubtasks(UUID idUser, UUID parentId)
            throws DataBaseException, NotFoundException {

        List<Task> remainingSubtasks = repository.findAllByIdParentTask(parentId);
        if (remainingSubtasks.isEmpty()) {
            Task parentTask = repository.findById(idUser, parentId);
            if (Boolean.TRUE.equals(parentTask.getHasSubtasks())) {
                parentTask.setHasSubtasks(false);
                repository.saveInDataBase(parentTask);
            }
        }
    }
}
