package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;

import java.sql.Date;
import java.util.UUID;

@Service
public class TaskService {
    public Object addTask(RequestTaskDTO dto) {
        // TODO: implement
        return null;
    }

    public Object repeatTask(UUID idUser, UUID idTask) {
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

    public Object lockTaskByObjective(UUID idUser, String idObjective) {
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
}
