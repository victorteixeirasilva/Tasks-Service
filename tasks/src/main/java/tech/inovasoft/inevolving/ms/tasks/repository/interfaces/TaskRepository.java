package tech.inovasoft.inevolving.ms.tasks.repository.interfaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface TaskRepository {


    Task saveInDataBase(Task task) throws DataBaseException;

    Task findById(UUID idUser, UUID idTask) throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException;

    boolean addNewTaskCopy(Task task, LocalDate currentDate) throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException, NotFoundException;

    ResponseMessageDTO deleteTask(Task task) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException;

    List<Task> findAllIsCopyTask(UUID idForSearch, Date date) throws DataBaseException;

    List<Task> findAllIsCopyTask(UUID idForSearch) throws DataBaseException;

    List<Task> findAllByIdObjective(UUID idForSearch) throws DataBaseException;

    List<Task> findAllByIdUserAndDateRange(UUID idUser, Date startDate, Date endDate) throws DataBaseException;

    List<Task> findAllByIdUserAndDate(UUID idUser, Date date) throws DataBaseException;

    List<Task> findAllByIdUserAndStatus(UUID idUser, String status) throws DataBaseException;

    List<Task> findAllByStatusAndDateRange(UUID idUser, Date startDate, Date endDate, String todo) throws DataBaseException;

    List<Task> findAllByStatusAndDate(UUID idUser, Date date, String todo) throws DataBaseException;

    List<Task> findAllByIdUserAndIdObjectiveAndDateRange(UUID idUser, UUID idObjective, Date startDate, Date endDate);
}
