package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.*;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private SimpleTaskService simpleTaskService;

    /**
     * @desciprion - Lock old tasks based on the goal, and delete tasks after the goal completion date. | Bloquear tarefas antigas com base no objetivo, e excluir as tarefas posteriores a data de conclusão do objetivo.
     * @param idUser - id of user | id do usuário
     * @param idObjective - id of objective | id do objetivo
     * @param completionDate - goal completion date | data de conclusão do objetivo
     * @return - Block confirmation | Confirmacao de bloqueio
     */
    public ResponseMessageDTO lockTaskByObjective(UUID idUser, UUID idObjective, Date completionDate) throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        List<Task> tasks = repository.findAllByIdObjective(idObjective);
        for (Task task : tasks) {
            if (!task.getIdUser().equals(idUser)) {
                throw new UserWithoutAuthorizationAboutTheTaskException();
            }
            if (task.getDateTask().after(completionDate)) {
                simpleTaskService.deleteTask(task.getId(), idUser);
            } else {
                task.setBlockedByObjective(true);
                repository.saveInDataBase(task);
            }
        }
        return new ResponseMessageDTO("Tasks locked!");
    }

    /**
     * @desciprion - Get tasks in date range | Obter tarefas no intervalo de datas
     * @param idUser - id of user | id do usuário
     * @param startDate - start date | data de inicio
     * @param endDate - end date | data de fim
     * @return - List of tasks | Lista de tarefas
     */
    public List<Task> getTasksInDateRange(UUID idUser, Date startDate, Date endDate) throws NotFoundTasksInDateRangeException, DataBaseException {
        List<Task> tasks = repository.findAllByIdUserAndDateRange(idUser, startDate, endDate);

        if (tasks.isEmpty()) {
            throw new NotFoundTasksInDateRangeException();
        }

        return tasks;
    }

    /**
     * @desciprion - Get tasks in date | Obter tarefas na data
     * @param idUser - id of user | id do usuário
     * @param date - date | data
     * @return - List of tasks | Lista de tarefas
     */
    public List<Task> getTasksInDate(UUID idUser, Date date) throws NotFoundTasksInDateException, DataBaseException {
        List<Task> tasks = repository.findAllByIdUserAndDate(idUser, date);

        if (tasks.isEmpty()) {
            throw new NotFoundTasksInDateException();
        }

        return tasks;
    }

    /**
     * @desciprion - Get tasks late | Obter tarefas atrasadas
     * @param idUser - id of user | id do usuário
     * @return - List of tasks | Lista de tarefas
     */
    public List<Task> getTasksLate(UUID idUser) throws NotFoundTasksWithStatusLateException, DataBaseException {
        List<Task> tasks = repository.findAllByIdUserAndStatus(idUser, Status.LATE);

        if (tasks.isEmpty()) {
            throw new NotFoundTasksWithStatusLateException();
        }

        return tasks;
    }

    /**
     * @desciprion - Get tasks by status in date range | Obter tarefas por status no intervalo de datas
     * @param idUser - id of user | id do usuário
     * @param startDate - start date | data de inicio
     * @param endDate - end date | data de fim
     * @param status - status | status
     * @return - List of tasks | Lista de tarefas
     */
    public List<Task> getTasksStatusInDateRange(UUID idUser, Date startDate, Date endDate, String status) throws NotFoundTasksWithStatusException, DataBaseException {
        List<Task> tasks = repository.findAllByStatusAndDateRange(idUser, startDate, endDate, status);

        if (tasks.isEmpty()) {
            throw new NotFoundTasksWithStatusException(status);
        }

        return tasks;
    }

    /**
     * @desciprion - Get tasks by status in date | Obter tarefas por status na data
     * @param idUser - id of user | id do usuário
     * @param date - date | data
     * @param status - status | status
     * @return - List of tasks | Lista de tarefas
     */
    public List<Task> getTasksStatusInDate(UUID idUser, Date date, String status) throws NotFoundTasksWithStatusException {
        List<Task> tasks = repository.findAllByStatusAndDate(idUser, date, status);

        if (tasks.isEmpty()) {
            throw new NotFoundTasksWithStatusException(status);
        }

        return tasks;
    }
}
