package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;

import java.sql.Date;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public ResponseTaskDTO addTask(RequestTaskDTO dto) throws DataBaseException {
        try {
            return new ResponseTaskDTO(repository.save(new Task(dto)));
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(save)");
        }
    }

    public Object repeatTask(UUID idUser, UUID idTask, DaysOfTheWeekDTO daysOfTheWeekDTO) {
        // TODO: implement
        return null;
    }

    public Object updateTask(UUID idUser, UUID idTask, RequestUpdateTaskDTO dto) {
        // TODO: implement
        return null;
    }

    public Object updateTasksAndTheirFutureRepetitions(UUID idUser, UUID idTask, RequestUpdateTaskDTO dto) {
        // TODO: implement
        return null;
    }

    public Object updateTaskStatus(UUID idUser, UUID idTask, String todo) {
        // TODO: implement
        return null;
    }

    public Object deleteTask(UUID idUser, UUID idTask) {
        // TODO: implement
        return null;
    }

    public Object deleteTasksAndTheirFutureRepetitions(UUID idUser, UUID idTask) {
        // TODO: implement
        return null;
    }

    public Object lockTaskByObjective(UUID idUser, UUID idObjective) {
        // TODO: implement
        return null;
    }

    public Object getTasksInDateRange(UUID idUser, Date startDate, Date endDate) {
        // TODO: implement
        return null;
    }

    public Object getTasksInDate(UUID idUser, Date date) {
        // TODO: implement
        return null;
    }

    public Object getTasksLate(UUID idUser) {
        // TODO: implement
        return null;
    }

    public Object getTasksStatusInDateRange(UUID idUser, Date startDate, Date endDate, String status) {
        // TODO: implement
        return null;
    }

    public Object getTasksStatusInDate(UUID idUser, Date date, String status) {
        // TODO: implement
        return null;
    }
}
