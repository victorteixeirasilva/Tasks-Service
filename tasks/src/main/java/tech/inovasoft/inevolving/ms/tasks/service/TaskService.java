package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.*;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.TaskRepository;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public Task saveInDataBase(Task task) throws DataBaseException {
        try {
            return repository.save(task);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(save)");
        }
    }

    public ResponseTaskDTO addTask(RequestTaskDTO dto) throws DataBaseException {
        return new ResponseTaskDTO(saveInDataBase(new Task(dto)));
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

    public boolean addNewTaskCopy(Task task, LocalDate currentDate) throws DataBaseException, UserWithoutAuthorizationAboutTheTaskException {
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

    public ResponseRepeatTaskDTO repeatTask(UUID idUser, UUID idTask, DaysOfTheWeekDTO daysOfTheWeekDTO, Date startDate, Date endDate) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        validRange(startDate, endDate);

        int numberRepetitions = 1;
        Task task = findById(idUser, idTask);

        Date currentDate = startDate;
        while (currentDate.before(endDate) || currentDate.equals(endDate)) {
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
        task.setIdObjective(dto.idObjective());


        if (task.getIsCopy()) {
            task.setIsCopy(false);
            task.setIdOriginalTask(null);
        }

        return new ResponseTaskDTO(saveInDataBase(task));
    }

    public Task saveUpdateTask(Task task, RequestUpdateRepeatTaskDTO dto, UUID idOriginalTask){
        task.setIdOriginalTask(idOriginalTask);
        task.setNameTask(dto.nameTask);
        task.setDescriptionTask(dto.descriptionTask);
        task.setIdObjective(dto.idObjective);

        return task;
    }

//    public ResponseUpdateRepeatTaskDTO updateTasksAndTheirFutureRepetitions(UUID idUser, UUID idTask, Date startDate, Date endDate, RequestUpdateRepeatTaskDTO dto) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
//        int numberRepetitions = 0;
//        int numberDeleteRepetitions = 0;
//        int numberUpdateRepetitions = 0;
//        int numberCreateRepetitions = 0;
//        Set<Task> deleteTasks = new HashSet<>();
//        Set<Task> updateTasks = new HashSet<>();
//
//        validRange(startDate, endDate);
//
//        Task task = findById(idUser, idTask);
//
//
//
//        UUID idForSearch = idTask;
//        if (task.getIsCopy()) {
//            idForSearch = task.getIdOriginalTask();
//            task.setIsCopy(false);
//            task.setIdOriginalTask(null);
//
//            task.setNameTask(dto.nameTask);
//            task.setDescriptionTask(dto.descriptionTask);
//            task.setIdObjective(dto.idObjective);
//
//            saveInDataBase(task);
//            numberRepetitions++;
//            numberUpdateRepetitions++;
//
//        } else {
//            updateTasks.add(task);
//        }
//
//        List<Task> allTasks;
//        try {
//            allTasks = repository.findAllByIdOriginalTaskAndIsCopy(idForSearch, task.getDateTask());
//        } catch (Exception e) {
//            //TODO: teste
//            throw new DataBaseException("(findAllByIdOriginalTaskAndIsCopy)");
//        }
//
//        Optional<Date> biggerDateInDB = allTasks
//                .stream()
//                .map(Task::getDateTask)
//                .max(Date::compareTo);
//
//        // Se a data final da atualização precisar de novas tarefas
//        if (biggerDateInDB.isPresent()){
//            if (biggerDateInDB.get().before(endDate)) {
//                var resultAddNewTasks = repeatTask(idUser, idTask, dto.daysOfTheWeekDTO, Date.valueOf(biggerDateInDB.get().toLocalDate().plusDays(1)), endDate);
//                numberCreateRepetitions += resultAddNewTasks.numberRepetitions();
//            }
//            for (Task t: allTasks) {
//                if ((t.getDateTask().after(endDate)) || (t.getDateTask().before(task.getDateTask()))){
//                    deleteTasks.add(t);
//                }
//            }
//        }
//        numberRepetitions += allTasks.size();
//        allTasks.removeAll(deleteTasks);
//
//        for (Task t: allTasks) {
//            Date currentDate = t.getDateTask();
//            if (currentDate.after(task.getDateTask())) {
//                LocalDate localDate = currentDate.toLocalDate();
//                DayOfWeek dayOfWeek = localDate.getDayOfWeek();
//                switch (dayOfWeek) {
//                    case MONDAY:
//                        if (dto.daysOfTheWeekDTO.isMonday()) {
//                            updateTasks.add(saveUpdateTask(t, dto, idTask));
//                        } else {
//                            LocalDate dateOldTask = t.getDateTask().toLocalDate();
//                            if (dateOldTask.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
//                                deleteTasks.add(t);
//                            }
//                        }
//                        break;
//                    case TUESDAY:
//                        if (dto.daysOfTheWeekDTO.isTuesday()) {
//                            updateTasks.add(saveUpdateTask(t, dto, idTask));
//                        } else {
//                            LocalDate dateOldTask = t.getDateTask().toLocalDate();
//                            if (dateOldTask.getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
//                                deleteTasks.add(t);
//                            }
//                        }
//                        break;
//                    case WEDNESDAY:
//                        if (dto.daysOfTheWeekDTO.isWednesday()) {
//                            updateTasks.add(saveUpdateTask(t, dto, idTask));
//                        } else {
//                            LocalDate dateOldTask = t.getDateTask().toLocalDate();
//                            if (dateOldTask.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
//                                deleteTasks.add(t);
//                            }
//                        }
//                        break;
//                    case THURSDAY:
//                        if (dto.daysOfTheWeekDTO.isThursday()) {
//                            updateTasks.add(saveUpdateTask(t, dto, idTask));
//                        } else {
//                            deleteTasks.add(t);
//                        }
//                        break;
//                    case FRIDAY:
//                        if (dto.daysOfTheWeekDTO.isFriday()) {
//                            updateTasks.add(saveUpdateTask(t, dto, idTask));
//                        } else {
//                            LocalDate dateOldTask = t.getDateTask().toLocalDate();
//                            if (dateOldTask.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
//                                deleteTasks.add(t);
//                            }
//                        }
//                        break;
//                    case SATURDAY:
//                        if (dto.daysOfTheWeekDTO.isSaturday()) {
//                            updateTasks.add(saveUpdateTask(t, dto, idTask));
//                        } else {
//                            LocalDate dateOldTask = t.getDateTask().toLocalDate();
//                            if (dateOldTask.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
//                                deleteTasks.add(t);
//                            }
//                        }
//                        break;
//                    case SUNDAY:
//                        if (dto.daysOfTheWeekDTO.isSunday()) {
//                            updateTasks.add(saveUpdateTask(t, dto, idTask));
//                        } else {
//                            LocalDate dateOldTask = t.getDateTask().toLocalDate();
//                            if (dateOldTask.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
//                                deleteTasks.add(t);
//                            }
//                        }
//                        break;
//                }
//            }
//        }
//
//
//        numberUpdateRepetitions += updateTasks.size();
//        numberDeleteRepetitions += deleteTasks.size();
//
//        for (Task t: updateTasks){
//            saveInDataBase(t);
//        }
//
//        for (Task t : deleteTasks){
//            deleteTask(idUser, t.getId());
//        }
//
//        var resultAddNewTasks = repeatTask(idUser, task.getId(), dto.daysOfTheWeekDTO, task.getDateTask(), endDate);
//        numberCreateRepetitions += resultAddNewTasks.numberRepetitions();
//
//        return new ResponseUpdateRepeatTaskDTO(numberRepetitions, numberDeleteRepetitions, numberUpdateRepetitions, numberCreateRepetitions);
//
//    }

    public ResponseUpdateRepeatTaskDTO updateTasksAndTheirFutureRepetitions(UUID idUser, UUID idTask, Date startDate, Date endDate, RequestUpdateRepeatTaskDTO dto) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        //TODO: Deletar tarefas futuras e adicionar novamente

        validRange(startDate, endDate);

        Task task = findById(idUser, idTask);



        UUID idForSearch = idTask;
        if (task.getIsCopy()) {
            idForSearch = task.getIdOriginalTask();
            task.setIsCopy(false);
            task.setIdOriginalTask(null);

            task.setNameTask(dto.nameTask);
            task.setDescriptionTask(dto.descriptionTask);
            task.setIdObjective(dto.idObjective);

            saveInDataBase(task);

        }

        List<Task> allTasks;
        try {
            allTasks = repository.findAllByIdOriginalTaskAndIsCopy(idForSearch, task.getDateTask());
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(findAllByIdOriginalTaskAndIsCopy)");
        }

        for (Task t : allTasks) {
            if (task.getDateTask().before(t.getDateTask())) {
                deleteTask(idUser, t.getId());
            }
        }

        ResponseRepeatTaskDTO result = repeatTask(idUser, idTask, dto.daysOfTheWeekDTO, task.getDateTask(), endDate);

        return new ResponseUpdateRepeatTaskDTO("Successfully update repeated tasks");

    }

    public void validRange(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            // TODO: teste.
            throw new DateTimeException("Start date must be before end date.");
        }
    }

    public ResponseTaskDTO updateTaskStatus(UUID idUser, UUID idTask, String todo) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        Task task = findById(idUser, idTask);
        task.setStatus(todo);
        return new ResponseTaskDTO(saveInDataBase(task));
    }

    public ResponseMessageDTO deleteTask(UUID idUser, UUID idTask) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        Task task = findById(idUser, idTask);
        try {
            repository.delete(task);
        } catch (Exception e) {
            //TODO: teste
            throw new DataBaseException("(delete)");
        }
        return new ResponseMessageDTO("Successfully delete task");
    }

    public ResponseDeleteTasksDTO deleteTasksAndTheirFutureRepetitions(UUID idUser, UUID idTask, Date date) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        int numberDeleteRepetitions = 0;
        Task task = findById(idUser, idTask);
        UUID idForSearch = idTask;

        if (task.getIsCopy()) {
            idForSearch = task.getIdOriginalTask();
        }

        deleteTask(idUser, idTask);
        numberDeleteRepetitions++;

        List<Task> tasks = repository.findAllByIdOriginalTaskAndIsCopy(idForSearch);
        if (!tasks.isEmpty()) {
            for (Task oldTask : tasks) {
                if (oldTask.getDateTask().after(date)){
                    deleteTask(idUser, oldTask.getId());
                    numberDeleteRepetitions++;
                }
            }
        }

        return new ResponseDeleteTasksDTO(numberDeleteRepetitions);
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
