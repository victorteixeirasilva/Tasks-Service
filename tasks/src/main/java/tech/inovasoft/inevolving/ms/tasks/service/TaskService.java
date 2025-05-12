package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public ResponseMessageDTO lockTaskByObjective(UUID idUser, UUID idObjective) {
        // TODO: implement
        return null;
    }

    public List<Task> getTasksInDateRange(UUID idUser, Date startDate, Date endDate) {
        // TODO: implement
        return null;
    }

    public List<Task> getTasksInDate(UUID idUser, Date date) {
        // TODO: implement
        return null;
    }

    public List<Task> getTasksLate(UUID idUser) {
        // TODO: implement
        return null;
    }

    public List<Task> getTasksStatusInDateRange(UUID idUser, Date startDate, Date endDate, String status) {
        // TODO: implement
        return null;
    }

    public List<Task> getTasksStatusInDate(UUID idUser, Date date, String status) {
        // TODO: implement
        return null;
    }
}
