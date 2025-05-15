package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.*;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;
import tech.inovasoft.inevolving.ms.tasks.service.client.ObjectivesServiceClient;

import java.util.*;
import java.util.concurrent.*;

@Service
public class SimpleTaskService {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private ObjectivesServiceClient objectivesServiceClient;

    /**
     * @desciprion - Method to validate whether the objective exists, by querying the objectives microservice. | Metodo para validar se o objetivo existe, fazendo a consulta ao micro serviço de objetivos.
     * @param idObjective - ID of the objective. | ID do objetivo.
     * @throws NotFoundException - Thrown when the objective is not found. | Lancado quando o objetivo nao eh encontrado.
     */
    public void validObjective(UUID idObjective) throws NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        ResponseEntity response = null;
        try {
            response = objectivesServiceClient.getObjectiveById(idObjective);
        } catch (Exception e) {
            throw new NotFoundException("Objective not found in objectives service");
        }

        if (response == null || !response.getStatusCode().is2xxSuccessful()){
            throw new NotFoundException("Objective not found in objectives service");
        }
    }

    /**
     * @desciprion - Method to add a new task (without repetition). | Metodo para adicionar uma nova tarefa (sem repeticao).
     * @param dto - DTO (Data Transfer Object) to add a new task. | DTO (Data Transfer Object) para adicionar uma nova tarefa.
     * @return - ResponseTaskDTO with the new task. | ResponseTaskDTO com a nova tarefa.
     */
    public ResponseTaskDTO addTask(RequestTaskDTO dto) throws DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        validObjective(dto.idObjective());
        return new ResponseTaskDTO(repository.saveInDataBase(new Task(dto)));
    }

    /**
     * @desciprion -  Method to update an existing task. | Metodo para atualizar uma tarefa existente.
     * @param idUser - ID of the user. | ID do usuário.
     * @param idTask - ID of the task. | ID da tarefa.
     * @param dto - DTO (Data Transfer Object) to update the task. | DTO (Data Transfer Object) para atualizar a tarefa.
     * @return - ResponseTaskDTO with the updated task. | ResponseTaskDTO com a tarefa atualizada.
     */
    public ResponseTaskDTO updateTask(UUID idUser, UUID idTask, RequestUpdateTaskDTO dto) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        Task task = repository.findById(idUser, idTask);
        task.setNameTask(dto.nameTask());
        task.setDescriptionTask(dto.descriptionTask());
        validObjective(dto.idObjective());
        task.setIdObjective(dto.idObjective());


        if (task.getIsCopy()) {
            task.setIsCopy(false);
            task.setIdOriginalTask(null);
        }

        return new ResponseTaskDTO(repository.saveInDataBase(task));
    }

    /**
     * @desciprion - Method to update the status of a task. | Metodo para atualizar o status de uma tarefa.
     * @param idUser - ID of the user. | ID do usuario.
     * @param idTask - ID of the task. | ID da tarefa.
     * @param status - Status of the task. | Status da tarefa.
     * @return - ResponseTaskDTO with the updated task. | ResponseTaskDTO com a tarefa atualizada.
     */
    public ResponseTaskDTO updateTaskStatus(UUID idUser, UUID idTask, String status) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        Task task = repository.findById(idUser, idTask);
        task.setStatus(status);
        return new ResponseTaskDTO(repository.saveInDataBase(task));
    }

    /**
     * @desciprion - Method to delete a task. | Metodo para deletar uma tarefa.
     * @param idUser - ID of the user. | ID do usuario.
     * @param idTask - ID of the task. | ID da tarefa.
     * @return - ResponseMessageDTO with the message of success. | ResponseMessageDTO com a mensagem de sucesso.
     */
    public ResponseMessageDTO deleteTask(UUID idUser, UUID idTask) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        Task task = repository.findById(idUser, idTask);
        return repository.deleteTask(task);
    }

}
