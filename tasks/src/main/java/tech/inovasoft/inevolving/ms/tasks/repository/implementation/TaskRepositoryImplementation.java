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
import java.util.UUID;

@Service
public class TaskRepositoryImplementation implements TaskRepository {

    @Autowired
    private JpaRepositoryInterface repository;

    public Task saveInDataBase(Task task) throws DataBaseException {
        try {
            return repository.save(task);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(save)");
        }
    }

    public Task findById(UUID idUser, UUID idTask) throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException {
        Optional<Task> taskOptional;
        try {
            taskOptional = repository.findById(idTask);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(findById)");
        }

        if (taskOptional.isEmpty()) {
            //TODO: teste
            throw new NotFoundException("(findById) Task not found");
        }

        if (!taskOptional.get().getIdUser().equals(idUser)) {
            //TODO: teste
            throw new UserWithoutAuthorizationAboutTheTaskException();
        }

        return taskOptional.get();
    }

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
                //TODO: teste
                throw new DataBaseException("(save)");
            }
        }
        return false;
    }

    public ResponseMessageDTO deleteTask(Task task) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        try {
            repository.delete(task);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(delete)");
        }
        return new ResponseMessageDTO("Successfully delete task");
    }

    public List<Task> findAllIsCopyTask(UUID idForSearch, Date date) throws DataBaseException {
        List<Task> allTasks;
        try {
            allTasks = repository.findAllByIdOriginalTaskAndIsCopy(idForSearch, date);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(findAllByIdOriginalTaskAndIsCopy)");
        }
        return allTasks;
    }

    public List<Task> findAllIsCopyTask(UUID idForSearch) throws DataBaseException {
        List<Task> allTasks;
        try {
            allTasks = repository.findAllByIdOriginalTaskAndIsCopy(idForSearch);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(findAllByIdOriginalTaskAndIsCopy)");
        }
        return allTasks;
    }

}
