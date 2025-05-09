package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
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

    public Task findById(UUID idUser, UUID idTask) throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException {
        Optional<Task> taskOptional;
        try {
            taskOptional = repository.findById(idTask);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(findById)");
        }

        if (taskOptional.isEmpty()) {
            //TODO: teste
            throw new DataBaseException("(findById) Task not found");
        }

        if (!taskOptional.get().getIdUser().equals(idUser)) {
            //TODO: teste
            throw new UserWithoutAuthorizationAboutTheTaskException();
        }

        return taskOptional.get();
    }

    public boolean addNewTaskCopy(Task task, LocalDate currentDate) throws DataBaseException {
        try {
            repository.save(new Task(
                    new RequestTaskDTO(
                            task.getNameTask(),
                            task.getDescriptionTask(),
                            currentDate,
                            Optional.ofNullable(task.getIdObjective()),
                            task.getIdUser()
                    ),
                    task.getId()
            ));
            return true;
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(save)");
        }
    }

    public ResponseRepeatTaskDTO repeatTask(UUID idUser, UUID idTask, DaysOfTheWeekDTO daysOfTheWeekDTO, Date startDate, Date endDate) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        if (startDate.after(endDate)) {
            // TODO: teste.
            throw new DateTimeException("Start date must be before end date.");
        }

        int numberRepetitions = 1;
        Task task = findById(idUser, idTask);

        Date currentDate = startDate;
        while (currentDate.before(endDate)) {
            if (currentDate.after(task.getDateTask())) {
                LocalDate localDate = currentDate.toLocalDate();
                DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                switch (dayOfWeek) {
                    case MONDAY:
                        if (daysOfTheWeekDTO.isMonday()) {
                            if (addNewTaskCopy(task, currentDate.toLocalDate())) numberRepetitions++;
                        }
                        break;
                    case TUESDAY:
                        if (daysOfTheWeekDTO.isTuesday()) {
                            if (addNewTaskCopy(task, currentDate.toLocalDate())) numberRepetitions++;
                        }
                        break;
                    case WEDNESDAY:
                        if (daysOfTheWeekDTO.isWednesday()) {
                            if (addNewTaskCopy(task, currentDate.toLocalDate())) numberRepetitions++;
                        }
                        break;
                    case THURSDAY:
                        if (daysOfTheWeekDTO.isThursday()) {
                            if (addNewTaskCopy(task, currentDate.toLocalDate())) numberRepetitions++;
                        }
                        break;
                    case FRIDAY:
                        if (daysOfTheWeekDTO.isFriday()) {
                            if (addNewTaskCopy(task, currentDate.toLocalDate())) numberRepetitions++;
                        }
                        break;
                    case SATURDAY:
                        if (daysOfTheWeekDTO.isSaturday()) {
                            if (addNewTaskCopy(task, currentDate.toLocalDate())) numberRepetitions++;
                        }
                        break;
                    case SUNDAY:
                        if (daysOfTheWeekDTO.isSunday()) {
                            if (addNewTaskCopy(task, currentDate.toLocalDate())) numberRepetitions++;
                        }
                        break;
                }
            }
            currentDate = Date.valueOf(currentDate.toLocalDate().plusDays(1));
        }

        return new ResponseRepeatTaskDTO(numberRepetitions);
    }

    public ResponseTaskDTO updateTask(UUID idUser, UUID idTask, RequestUpdateTaskDTO dto) {
        // TODO: implement
        return null;
    }

    public Object updateTasksAndTheirFutureRepetitions(UUID idUser, UUID idTask, Date startDate, Date endDate, RequestUpdateRepeatTaskDTO dto) {
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
