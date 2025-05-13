package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.*;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringTaskService {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private SimpleTaskService simpleTaskService;

    /**
     * @desciprion - add new tasks | adicionar uma nova tarefa.
     * @param idUser - ID of the user. | ID do usuário.
     * @param idTask - ID of the task. | ID da tarefa.
     * @param daysOfTheWeekDTO - DTO (Data Transfer Object) of days of the week. | DTO (Data Transfer Object) de dias da semana.
     * @param startDate - start date. | data de inicio.
     * @param endDate - end date. | data de fim.
     * @return - number of repetitions. | quantidade de repeticoes.
     */
    public ResponseRepeatTaskDTO addTasks(UUID idUser, UUID idTask, DaysOfTheWeekDTO daysOfTheWeekDTO, Date startDate, Date endDate) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        validDateRange(startDate, endDate);

        int numberRepetitions = 1;
        Task task = repository.findById(idUser, idTask);

        Date currentDate = startDate;
        while (currentDate.before(endDate) || currentDate.equals(endDate)) {
            if (currentDate.after(task.getDateTask())) {
                LocalDate localDate = currentDate.toLocalDate();
                DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                switch (dayOfWeek) {
                    case MONDAY:
                        if (daysOfTheWeekDTO.isMonday()) {
                            repository.addNewTaskCopy(task, currentDate.toLocalDate());
                            numberRepetitions++;
                        }
                        break;
                    case TUESDAY:
                        if (daysOfTheWeekDTO.isTuesday()) {
                            repository.addNewTaskCopy(task, currentDate.toLocalDate());
                            numberRepetitions++;
                        }
                        break;
                    case WEDNESDAY:
                        if (daysOfTheWeekDTO.isWednesday()) {
                            repository.addNewTaskCopy(task, currentDate.toLocalDate());
                            numberRepetitions++;
                        }
                        break;
                    case THURSDAY:
                        if (daysOfTheWeekDTO.isThursday()) {
                            repository.addNewTaskCopy(task, currentDate.toLocalDate());
                            numberRepetitions++;
                        }
                        break;
                    case FRIDAY:
                        if (daysOfTheWeekDTO.isFriday()) {
                            repository.addNewTaskCopy(task, currentDate.toLocalDate());
                            numberRepetitions++;
                        }
                        break;
                    case SATURDAY:
                        if (daysOfTheWeekDTO.isSaturday()) {
                            repository.addNewTaskCopy(task, currentDate.toLocalDate());
                            numberRepetitions++;
                        }
                        break;
                    case SUNDAY:
                        if (daysOfTheWeekDTO.isSunday()) {
                            repository.addNewTaskCopy(task, currentDate.toLocalDate());
                            numberRepetitions++;
                        }
                        break;
                }
            }
            currentDate = Date.valueOf(currentDate.toLocalDate().plusDays(1));
        }

        return new ResponseRepeatTaskDTO(numberRepetitions);
    }

    /**
     * @desciprion - Update tasks | Atualizar tarefas.
     * @param idUser - ID of the user. | ID do usuário.
     * @param idTask - ID of the task. | ID da tarefa.
     * @param endDate - end date. | data de fim.
     * @param dto - DTO (Data Transfer Object) of update tasks. | DTO (Data Transfer Object) de atualizacao de tarefas.
     * @return - Return a confirmation message that the process was successful. | Retorna uma mensagem de confirmação que o precesso foi realizado com sucesso.
     */
    public ResponseUpdateRepeatTaskDTO updateTasks(UUID idUser, UUID idTask, Date endDate, RequestUpdateRepeatTaskDTO dto) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {

        Task task = repository.findById(idUser, idTask);

        validDateRange(task.getDateTask(), endDate);


        UUID idForSearch = idTask;
        if (task.getIsCopy()) {
            idForSearch = task.getIdOriginalTask();
            task.setIsCopy(false);
            task.setIdOriginalTask(null);

            task.setNameTask(dto.nameTask);
            task.setDescriptionTask(dto.descriptionTask);
            task.setIdObjective(dto.idObjective);

            repository.saveInDataBase(task);

        }

        List<Task> allTasks = repository.findAllIsCopyTask(idForSearch, task.getDateTask());

        for (Task t : allTasks) {
            if (task.getDateTask().before(t.getDateTask())) {
                simpleTaskService.deleteTask(idUser, t.getId());
            }
        }

        ResponseRepeatTaskDTO result = addTasks(idUser, idTask, dto.daysOfTheWeekDTO, task.getDateTask(), endDate);

        return new ResponseUpdateRepeatTaskDTO("Successfully update repeated tasks");

    }

    /**
     * @desciprion - Check if the start date is before the end date. | Verifique se a data de inicio eh antes da data de fim.
     * @param startDate - start date. | data de inicio.
     * @param endDate - end date. | data de fim.
     */
    private void validDateRange(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            throw new DateTimeException("Start date must be before end date.");
        }
    }

    /**
     * @desciprion - Delete tasks | Deletar tarefas.
     * @param idUser - ID of the user. | ID do usuário.
     * @param idTask - ID of the task. | ID da tarefa.
     * @param date - start date. | A partir da data.
     * @return - Return a confirmation message that the process was successful. | Retorna uma mensagem de confirmação que o precesso foi realizado com sucesso.
     */
    public ResponseDeleteTasksDTO deleteTasks(UUID idUser, UUID idTask, Date date) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        int numberDeleteRepetitions = 0;
        Task task = repository.findById(idUser, idTask);
        UUID idForSearch = idTask;

        if (task.getIsCopy()) {
            idForSearch = task.getIdOriginalTask();
        }

        simpleTaskService.deleteTask(idUser, idTask);
        numberDeleteRepetitions++;

        List<Task> tasks = repository.findAllIsCopyTask(idForSearch);
        if (!tasks.isEmpty()) {
            for (Task oldTask : tasks) {
                if (oldTask.getDateTask().after(date)){
                    simpleTaskService.deleteTask(idUser, oldTask.getId());
                    numberDeleteRepetitions++;
                }
            }
        }

        return new ResponseDeleteTasksDTO(numberDeleteRepetitions);
    }

}
