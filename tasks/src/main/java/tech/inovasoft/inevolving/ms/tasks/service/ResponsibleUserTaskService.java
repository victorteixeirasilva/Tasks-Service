package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateResponsibleUserDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseResponsibleUserDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.util.UUID;

@Service
public class ResponsibleUserTaskService {

    @Autowired
    private TaskRepository repository;

    public ResponseResponsibleUserDTO updateResponsibleUser(RequestUpdateResponsibleUserDTO dto)
            throws DataBaseException, NotFoundException {
        Task task = repository.findById(dto.idUser(), dto.idTask());
        task.setIdResponsibleUser(dto.idResponsibleUser());
        return new ResponseResponsibleUserDTO(repository.saveInDataBase(task));
    }

    public ResponseResponsibleUserDTO getResponsibleUser(UUID idUser, UUID idTask)
            throws DataBaseException, NotFoundException {
        Task task = repository.findById(idUser, idTask);
        return new ResponseResponsibleUserDTO(task);
    }
}
