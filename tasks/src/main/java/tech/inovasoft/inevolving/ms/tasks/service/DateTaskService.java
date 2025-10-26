package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateDateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.sql.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class DateTaskService {

    @Autowired
    private TaskRepository repository;

    public ResponseTaskDTO updateDateTask(RequestUpdateDateTaskDTO dto) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        Task task = repository.findById(dto.idTask(), dto.idUser());

        task.setDateTask(Date.valueOf(dto.dateTask()));

        return new ResponseTaskDTO(repository.saveInDataBase(task));
    }
}
