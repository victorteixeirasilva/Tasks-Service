package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;

import java.sql.Date;
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

    public Boolean addNewTask(Task task, Date currentDate) throws DataBaseException {
        Task newTask = new Task(task);
        newTask.setDateTask(currentDate);

        try {
            repository.save(task);
            return true;
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(save)");
        }
    }

    public ResponseRepeatTaskDTO repeatTask(UUID idUser, UUID idTask, DaysOfTheWeekDTO daysOfTheWeekDTO, Date startDate, Date endDate) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        if (startDate.before(endDate)) {
            // TODO: erro de datas.
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
                            if (addNewTask(task, currentDate)) {
                                numberRepetitions++;
                            }
                        }
                        break;
                    case TUESDAY:
                        if (daysOfTheWeekDTO.isTuesday()) {
                            if (addNewTask(task, currentDate)) {
                                numberRepetitions++;
                            }
                        }
                        break;
                    case WEDNESDAY:
                        if (daysOfTheWeekDTO.isWednesday()) {
                            if (addNewTask(task, currentDate)) {
                                numberRepetitions++;
                            }
                        }
                        break;
                    case THURSDAY:
                        if (daysOfTheWeekDTO.isThursday()) {
                            if (addNewTask(task, currentDate)) {
                                numberRepetitions++;
                            }
                        }
                        break;
                    case FRIDAY:
                        if (daysOfTheWeekDTO.isFriday()) {
                            if (addNewTask(task, currentDate)) {
                                numberRepetitions++;
                            }
                        }
                        break;
                    case SATURDAY:
                        if (daysOfTheWeekDTO.isSaturday()) {
                            if (addNewTask(task, currentDate)) {
                                numberRepetitions++;
                            }
                        }
                        break;
                    case SUNDAY:
                        if (daysOfTheWeekDTO.isSunday()) {
                            if (addNewTask(task, currentDate)) {
                                numberRepetitions++;
                            }
                        }
                        break;
                }
            }
            currentDate = Date.valueOf(currentDate.toLocalDate().plusDays(1));
        }

        return new ResponseRepeatTaskDTO(numberRepetitions);
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
