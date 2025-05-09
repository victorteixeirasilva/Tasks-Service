package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseUpdateRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
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

    public ResponseTaskDTO updateTask(UUID idUser, UUID idTask, RequestUpdateTaskDTO dto) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        Task task = findById(idUser, idTask);
        task.setNameTask(dto.nameTask());
        task.setDescriptionTask(dto.descriptionTask());

        if ((task.getIdObjective() != null) && dto.idObjective().isEmpty()) {
            task.setIdObjective(null);
        } else if (dto.idObjective().isPresent()){
            task.setIdObjective(dto.idObjective().get());
        }

        if ((task.getIsCopy()) && (task.getIdOriginalTask() != null)) {
            task.setIsCopy(false);
            task.setIdOriginalTask(null);
        }

        return addTask(new RequestTaskDTO(task));
    }

    public boolean saveUpdateRepeatTask(RequestUpdateRepeatTaskDTO dto, Task oldTask, UUID idTask) throws DataBaseException {
        //TODO: falta testar
        oldTask.setIdOriginalTask(idTask);
        oldTask.setNameTask(dto.nameTask);
        oldTask.setDescriptionTask(dto.descriptionTask);

        if ((oldTask.getIdObjective() != null) && (dto.idObjective.isEmpty())) {
            oldTask.setIdObjective(null);
        } else dto.idObjective.ifPresent(oldTask::setIdObjective);

        try {
            repository.save(oldTask);
            return true;
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(save)");
        }
    }

    public ResponseUpdateRepeatTaskDTO updateTasksAndTheirFutureRepetitions(UUID idUser, UUID idTask, Date startDate, Date endDate, RequestUpdateRepeatTaskDTO dto) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        int numberRepetitions = 1;
        int numberDeleteRepetitions = 0;
        int numberUpdateRepetitions = 1;
        int numberCreateRepetitions = 0;

        if (startDate.after(endDate)) {
            // TODO: teste.
            throw new DateTimeException("Start date must be before end date.");
        }

        Task task = findById(idUser, idTask);
        UUID idForSearch = idTask;
        if (task.getIsCopy()){
            idForSearch = task.getIdOriginalTask();
            task.setIsCopy(false);
            task.setIdOriginalTask(null);
        }
        task.setNameTask(dto.nameTask);
        task.setDescriptionTask(dto.descriptionTask);
        if (task.getIdObjective() != null) {
            task.setIdObjective(null);
            dto.idObjective.ifPresent(task::setIdObjective);
        }
        try {
            repository.save(task);
        } catch (Exception e) {
            throw new DataBaseException("(save)");
        }

        List<Task> oldTasks;
        try {
            oldTasks = repository.findAllByIdOriginalTaskAndIsCopy(idForSearch);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(findAllByIdOriginalTaskAndIsCopy)");
        }
        numberRepetitions += oldTasks.size();

        Optional<Date> maxOldDate = oldTasks
                .stream()
                .map(Task::getDateTask)
                .max(Date::compareTo);

        if (maxOldDate.isPresent()) {
            if (maxOldDate.get().before(endDate)) {
                var resultAddNewTasks = repeatTask(idUser, task.getId(), dto.daysOfTheWeekDTO, maxOldDate.get(), endDate);
                numberRepetitions += resultAddNewTasks.numberRepetitions();
                numberCreateRepetitions += resultAddNewTasks.numberRepetitions();
            }
        }

        Date currentDate = task.getDateTask();
        for (Task oldTask : oldTasks) {
                if (currentDate.after(task.getDateTask())) {
                    LocalDate localDate = currentDate.toLocalDate();
                    DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                    switch (dayOfWeek) {
                        case MONDAY:
                            if (dto.daysOfTheWeekDTO.isMonday()) {
                                if (saveUpdateRepeatTask(dto, oldTask, idTask)) {
                                    numberUpdateRepetitions++;
                                }
                            }
                            break;
                        case TUESDAY:
                            if (dto.daysOfTheWeekDTO.isTuesday()) {
                                if (saveUpdateRepeatTask(dto, oldTask, idTask)) {
                                    numberUpdateRepetitions++;
                                }
                            }
                            break;
                        case WEDNESDAY:
                            if (dto.daysOfTheWeekDTO.isWednesday()) {
                                if (saveUpdateRepeatTask(dto, oldTask, idTask)) {
                                    numberUpdateRepetitions++;
                                }
                            }
                            break;
                        case THURSDAY:
                            if (dto.daysOfTheWeekDTO.isThursday()) {
                                if (saveUpdateRepeatTask(dto, oldTask, idTask)) {
                                    numberUpdateRepetitions++;
                                }
                            }
                            break;
                        case FRIDAY:
                            if (dto.daysOfTheWeekDTO.isFriday()) {
                                if (saveUpdateRepeatTask(dto, oldTask, idTask)) {
                                    numberUpdateRepetitions++;
                                }
                            }
                            break;
                        case SATURDAY:
                            if (dto.daysOfTheWeekDTO.isSaturday()) {
                                if (saveUpdateRepeatTask(dto, oldTask, idTask)) {
                                    numberUpdateRepetitions++;
                                }
                            }
                            break;
                        case SUNDAY:
                            if (dto.daysOfTheWeekDTO.isSunday()) {
                                if (saveUpdateRepeatTask(dto, oldTask, idTask)) {
                                    numberUpdateRepetitions++;
                                }
                            }
                            break;
                    }
                }
                currentDate = Date.valueOf(currentDate.toLocalDate().plusDays(1));
        }

        if (maxOldDate.isPresent()) {
            for (Task oldTask : oldTasks) {
                if ((oldTask.getDateTask().after(endDate)) || (oldTask.getDateTask().before(task.getDateTask()))) {
//                  TODO: Refatorar para o método deleteTask(idUser, oldTask.getId());
                    repository.delete(oldTask);
                    numberDeleteRepetitions++;
                }
            }
        }



        return new ResponseUpdateRepeatTaskDTO(numberRepetitions, numberDeleteRepetitions, numberUpdateRepetitions, numberCreateRepetitions);
    }

    public ResponseTaskDTO updateTaskStatus(UUID idUser, UUID idTask, String todo) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
//        Task task = findById(idUser, idTask);
//        task.setStatus(todo);
//        return new ResponseTaskDTO(task);
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
