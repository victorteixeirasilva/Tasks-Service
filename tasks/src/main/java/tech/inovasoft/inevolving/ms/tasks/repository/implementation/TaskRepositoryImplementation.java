package tech.inovasoft.inevolving.ms.tasks.repository.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.JpaRepositoryInterface;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
public class TaskRepositoryImplementation implements TaskRepository {

    @Autowired
    private JpaRepositoryInterface repository;

    /**
     * @desciprion - This method is responsible for saving a new task in the database, and notifying you if there is any problem. (Esse method é responsável por salvar uma nova tarefa no banco de dados, e avisar caso tenha algum problema.)
     * @param task - Task that will be saved in the database. (Tarefa que será salva no banco de dados.)
     * @return - Returns the task saved in the database. (Tarefa salva no banco de dados.)
     * @throws DataBaseException - Error occurs if there is a problem saving the task in the bank. (Erro acontece caso tenha algum problema para salvar a tarefa no banco.)
     */
    public Task saveInDataBase(Task task) throws DataBaseException {
        try {
            return repository.save(task);
        } catch (Exception e) {
            throw new DataBaseException("(save)", e.getCause());
        }
    }

    /**
     * @desciprion - Method responsible for finding the task by its ID in the database. (method responsável por encontrar a tarefa pelo seu id no banco de dados.)
     * @param idUser - ID of the user who is looking for the task. (ID do usuário que está procurando a tarefa.)
     * @param idTask - ID of the task being searched. (ID da tarefa sendo procurada.)
     * @return - Returns the task found in the database. (Tarefa encontrada no banco de dados.)
     * @throws DataBaseException - Error occurs if there is a problem in the DBMS finding the task in the bank. (Erro acontece caso tenha algum problema no SGBD para encontrar a tarefa no banco.)
     * @throws UserWithoutAuthorizationAboutTheTaskException - Error occurs if the user does not have authorization to access the task. (Erro acontece caso o usuário não tenha autorização para acessar a tarefa.)
     * @throws NotFoundException - Error occurs if the task is not found in the database. (Erro acontece caso a tarefa nao seja encontrada no banco de dados.)
     */
    public Task findById(UUID idUser, UUID idTask) throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        Optional<Task> taskOptional;
        try {
            taskOptional = repository.findById(idTask);
        } catch (Exception e) {
            throw new DataBaseException("(findById)", e.getCause());
        }

        if (taskOptional.isEmpty()) {
            throw new NotFoundException("(findById) Task not found");
        }

        if (!taskOptional.get().getIdUser().equals(idUser)) {
            throw new UserWithoutAuthorizationAboutTheTaskException();
        }

        return taskOptional.get();
    }

    /**
     * @desciprion - method responsible for saving a copy of a task repetition in the database. (method responsável por salvar uma cópia de uma repetição da task, no banco de dados.)
     * @param task - Original task that will be repeated. (Task original que vai ser repetida.)
     * @param currentDate - Current date. (Data atual.)
     * @return - Returns true if the task was successfully saved in the database. (Retorna true se a tarefa foi salva com sucesso no banco de dados.)
     * @throws DataBaseException - Error occurs if there is a problem saving the task in the bank. (Erro acontece caso tenha algum problema para salvar a tarefa no banco.)
     */
    public boolean addNewTaskCopy(Task task, LocalDate currentDate) throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        Date sqlDate = Date.valueOf(currentDate);
        Optional<Task> taskOptional = repository.findByIdOriginalTaskOrIdTask(sqlDate, task.getId());

        if (taskOptional.isEmpty()) {
            Task newTask = findById(task.getIdUser(), task.getId());
            try {
                repository.save(new Task(
                        new RequestTaskDTO(
                                newTask.getNameTask(),
                                newTask.getDescriptionTask(),
                                currentDate,
                                newTask.getIdObjective(),
                                newTask.getIdUser()
                        ),
                        newTask.getId()
                ));
                return true;
            } catch (Exception e) {
                throw new DataBaseException("(save)");
            }
        }
        return false;
    }

    /**
     * @desciprion - Method responsible for deleting a task from the database. (method responsável por deletar uma tarefa do banco de dados.)
     * @param task - Task that will be deleted from the database. (Tarefa que sera deletada do banco de dados.)
     * @return - Returns a message indicating that the task was deleted successfully. (Mensagem que indica que a tarefa foi deletada com sucesso.)
     * @throws DataBaseException - Error occurs if there is a problem deleting the task in the bank. (Erro acontece caso tenha algum problema para deletar a tarefa no banco.)
     */
    public ResponseMessageDTO deleteTask(Task task) throws DataBaseException {
        try {
            repository.delete(task);
        } catch (Exception e) {
            throw new DataBaseException("(delete)");
        }
        return new ResponseMessageDTO("Successfully delete task");
    }

    /**
     * @desciprion - Method responsible for finding all tasks that are copies of a specific task. (method responsável por encontrar todas as tarefas que são copias de uma task especifica.)
     * @param idForSearch - ID of the task that will be searched. (ID da task que vai ser procurada.)
     * @param date - Date of the task that will be searched. (Data da task que vai ser procurada.)
     * @return - Returns a list of tasks that are copies of the task. (Lista de tarefas que são copias da task.)
     * @throws DataBaseException - Error occurs if there is a problem in the DBMS finding the task in the bank. (Erro acontece caso tenha algum problema no SGBD para encontrar a tarefa no banco.)
     */
    public List<Task> findAllIsCopyTask(UUID idForSearch, Date date) throws DataBaseException {
        List<Task> allTasks;
        try {
            allTasks = repository.findAllByIdOriginalTaskAndIsCopy(idForSearch, date);
        } catch (Exception e) {
            throw new DataBaseException("(findAllByIdOriginalTaskAndIsCopy)");
        }
        return allTasks;
    }

    /**
     * @desciprion - Method responsible for finding all tasks that are copies of a specific task. (method responsável por encontrar todas as tarefas que são copias de uma task especifica.)
     * @param idForSearch - ID of the task that will be searched. (ID da task que vai ser procurada.)
     * @return - Returns a list of tasks that are copies of the task. (Lista de tarefas que são copias da task.)
     * @throws DataBaseException - Error occurs if there is a problem in the DBMS finding the task in the bank. (Erro acontece caso tenha algum problema no SGBD para encontrar a tarefa no banco.)
     */
    public List<Task> findAllIsCopyTask(UUID idForSearch) throws DataBaseException {
        List<Task> allTasks;
        try {
            allTasks = repository.findAllByIdOriginalTaskAndIsCopy(idForSearch);
        } catch (Exception e) {
            throw new DataBaseException("(findAllByIdOriginalTaskAndIsCopy)");
        }
        return allTasks;
    }

    /**
     * @desciprion - Method responsible for finding all tasks of a specific objective | Metodo responsible for procurar todas as tarefas de um determinado objetivo.
     * @param idObjective - ID of the objective that will be searched. (ID do objetivo que vai ser procurado.)
     * @return - Returns a list of tasks that belong to the objective. (Lista de tarefas que pertencem ao objetivo.)
     * @throws DataBaseException - Error occurs if there is a problem in the DBMS finding the task in the bank. (Erro acontece caso tenha algum problema no SGBD para encontrar a tarefa no banco.)
     */
    @Override
    public List<Task> findAllByIdObjective(UUID idObjective) throws DataBaseException {
        List<Task> tasks;
        try {
            tasks = repository.findAllByIdObjective(idObjective);
        } catch (Exception e) {
            throw new DataBaseException("(findAllByIdObjective)", e.getCause());
        }
        return tasks;
    }

    @Override
    public List<Task> findAllByIdUserAndDateRange(UUID idUser, Date startDate, Date endDate) {
        // TODO: Crie o teste que falhe.
        // TODO: Faça o minimo para o teste passar
        // TODO: Refatore o codigo.
        return List.of();
    }

    @Override
    public List<Task> findAllByIdUserAndDate(UUID idUser, Date date) {
        // TODO: Crie o teste que falhe.
        // TODO: Faça o minimo para o teste passar
        // TODO: Refatore o codigo.
        return List.of();
    }

    @Override
    public List<Task> findAllByIdUserAndStatus(UUID idUser, String status) {
        // TODO: Crie o teste que falhe.
        // TODO: Faça o minimo para o teste passar
        // TODO: Refatore o codigo.
        return List.of();
    }

}
