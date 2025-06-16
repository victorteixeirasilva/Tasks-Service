package tech.inovasoft.inevolving.ms.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.*;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.*;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.service.RecurringTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Tag(name = "Tasks", description = "Task endpoint manager | Gerenciador dos end-points de tarefas")
@RestController
@RequestMapping("/ms/tasks")
public class TaskController {

    @Autowired
    private SimpleTaskService simpleTaskService;

    @Autowired
    private TaskService service;

    @Autowired
    private RecurringTaskService recurringTaskService;

    @Operation(
            summary = "Add a new task | Adicionar uma nova tarefa",
            description = "Returns the registered task. | Retorna a tarefa cadastrada."
    )
    @Async("asyncExecutor")
    @PostMapping
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> addTask(
            @RequestBody RequestTaskDTO taskDTO
    ) throws DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        simpleTaskService.addTask(taskDTO)
        ));
    }

    @Operation(
            summary = "Repeat a task | Repetir uma tarefa",
            description = "Returns the number of times the task was repeated | Retorna a quantidade de vezes que a tarefa foi repetida"
    )
    @Async("asyncExecutor")
    @PostMapping("/repeat/{idUser}/{idTask}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity<ResponseRepeatTaskDTO>> repeatTask(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable Date startDate,
            @PathVariable Date endDate,
            @RequestBody DaysOfTheWeekDTO daysOfTheWeekDTO
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        recurringTaskService.addTasks(idUser, idTask, daysOfTheWeekDTO, startDate, endDate)
        ));
    }

    @Operation(
            summary = "Update a task | Atualizar uma tarefa",
            description = "Returns the updated task. | Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PutMapping("/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> updateTask(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @RequestBody RequestUpdateTaskDTO updateTaskDTO
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        simpleTaskService.updateTask(idUser, idTask, updateTaskDTO)
        ));
    }

    @Operation(
            summary = "Update a task | Atualizar uma tarefas",
            description = "Returns updated tasks. | Retorna as tarefas atualizadas."
    )
    @Async("asyncExecutor")
    @PutMapping("/repeat/{idUser}/{idTask}/{endDate}")
    public CompletableFuture<ResponseEntity<ResponseUpdateRepeatTaskDTO>> updateTasksAndTheirFutureRepetitions(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable Date endDate,
            @RequestBody RequestUpdateRepeatTaskDTO updateTaskDTO
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        recurringTaskService.updateTasks(idUser, idTask, endDate, updateTaskDTO)
        ));
    }

    @Operation(
            summary = "Update status to To Do | Atualizar o status para To Do",
            description = "Returns the updated task. | Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/todo/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> updateTaskStatusToDo(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                simpleTaskService.updateTaskStatus(idUser, idTask, Status.TODO)
        ));
    }

    @Operation(
            summary = "Update status to In Progress | Atualizar o status para In Progress",
            description = "Returns the updated task. | Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/progress/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> updateTaskStatusInProgress(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                simpleTaskService.updateTaskStatus(idUser, idTask, Status.IN_PROGRESS)
        ));
    }

    @Operation(
            summary = "Update status to Done | Atualizar o status para Done",
            description = "Returns the updated task. | Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/done/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> updateTaskStatusDone(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                simpleTaskService.updateTaskStatus(idUser, idTask, Status.DONE)
        ));
    }

    @Operation(
            summary = "Update status to Late | Atualizar o status para Late",
            description = "Returns the updated task. | Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/late/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> updateTaskStatusLate(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                simpleTaskService.updateTaskStatus(idUser, idTask, Status.LATE)
        ));
    }

    @Operation(
            summary = "Update status to Canceled | Atualizar o status para Canceled",
            description = "Returns the updated task. | Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/canceled")
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> updateTaskStatusCanceled(
            @RequestBody RequestCanceledDTO dto
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                simpleTaskService.updateTaskStatusCancelled(dto)
        ));
    }

    @Operation(
            summary = "Remove a task | Remover uma tarefa",
            description = "Returns confirmation that the task has been removed. | Retorna confirmação que a tarefa foi removida."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity<ResponseMessageDTO>> deleteTask(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                simpleTaskService.deleteTask(idUser, idTask)
        ));
    }

    @Operation(
            summary = "Remove a task | Remover uma tarefas",
            description = "Returns confirmation that tasks have been removed, and the number of tasks removed. | Retorna confirmação que as tarefas foram removidas, e a quantidade de tarefas removidas."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/repeat/{idUser}/{idTask}/{date}")
    public CompletableFuture<ResponseEntity<ResponseDeleteTasksDTO>> deleteTasksAndTheirFutureRepetitions(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable Date date
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                recurringTaskService.deleteTasks(idUser, idTask, date)
        ));
    }

    @Operation(
            summary = "Block tasks based on a goal to keep a history | Bloquear tarefas com base em um objetivo para manter um historico",
            description = "Returns confirmation that tasks were blocked, the number of tasks removed, and the number of tasks blocked. | Retorna confirmação que as tarefas foram bloqueadas, e a quantidade de tarefas removidas, e a quantidade de tarefas bloqueadas."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/lock/{completionDate}/{idUser}/{idObjective}")
    public CompletableFuture<ResponseEntity<ResponseMessageDTO>> lockTaskByObjective(
            @PathVariable Date completionDate,
            @PathVariable UUID idUser,
            @PathVariable UUID idObjective
    ) throws UserWithoutAuthorizationAboutTheTaskException, NotFoundException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        service.lockTaskByObjective(idUser, idObjective, completionDate)
        ));
    }

    @Operation(
            summary = "See all tasks | Ver todas as tarefas",
            description = "Returns a list of all user tasks within the given date range. | Retorna uma lista de todas as tarefas do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksInDateRangeException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.getTasksInDateRange(idUser, startDate, endDate)
        ));
    }

    @Operation(
            summary = "View all tasks for a specific goal within a date range | Ver todas as tarefas de um objetivo especifico em um intervalo de datas",
            description = "Returns a list of all user tasks within the given date range. | Retorna uma lista de todas as tarefas do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/{idUser}/{idObjective}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksInDateRangeByObjectiveId(
            @PathVariable UUID idUser,
            @PathVariable UUID idObjective,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws DataBaseException, NotFoundTasksWithObjectiveException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        service.getTasksInDateRangeByObjectiveId(idUser, idObjective,startDate, endDate)
        ));
    }

    @Async("asyncExecutor")
    @GetMapping("/{idUser}/{idObjective}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksByObjectiveId(
            @PathVariable UUID idUser,
            @PathVariable UUID idObjective
    ) throws DataBaseException, NotFoundTasksWithObjectiveException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.getTasksByObjectiveId(idUser, idObjective)
        ));
    }

    @Operation(
            summary = "View all tasks from a date | Ver todas as tarefas de uma data",
            description = "Returns a list of all the user's tasks for the given date. | Retorna uma lista de todas as tarefas do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/{idUser}/{date}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksInDateException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.getTasksInDate(idUser, date)
        ));
    }

    @Operation(
            summary = "View all overdue tasks | Ver todas as tarefas atrasadas",
            description = "Returns a list of all the user's overdue tasks. | Retorna uma lista de todas as tarefas atrasadas do usuário."
    )
    @Async("asyncExecutor")
    @GetMapping("/late/{idUser}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksLate(
            @PathVariable UUID idUser
    ) throws NotFoundTasksWithStatusLateException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.getTasksLate(idUser)
        ));
    }

    @Operation(
            summary = "View all TODO tasks | Ver todas as tarefas TODO",
            description = "Returns a list of all user TODO tasks within the given date range. | Retorna uma lista de todas as tarefas TODO do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/todo/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksToDoInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.TODO)
        ));
    }

    @Operation(
            summary = "View all TODO tasks from a date | Ver todas as tarefas TODO de uma data",
            description = "Returns a list of all the user's TODO tasks for the given date. | Retorna uma lista de todas as tarefas TODO do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/todo/{idUser}/{date}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksToDoInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.getTasksStatusInDate(idUser, date, Status.TODO)
        ));
    }

    @Operation(
            summary = "View all tasks IN PROGRESS | Ver todas as tarefas IN PROGRESS",
            description = "Returns a list of all IN PROGRESS tasks for the user within the given date range. | Retorna uma lista de todas as tarefas IN PROGRESS do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/progress/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksInProgressInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.IN_PROGRESS)
        ));
    }

    @Operation(
            summary = "View all IN PROGRESS tasks for a date | Ver todas as tarefas IN PROGRESS de uma data",
            description = "Returns a list of all the user's IN PROGRESS tasks for the given date. | Retorna uma lista de todas as tarefas IN PROGRESS do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/progress/{idUser}/{date}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksInProgressInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        service.getTasksStatusInDate(idUser, date, Status.IN_PROGRESS)
        ));
    }

    @Operation(
            summary = "View all DONE tasks | Ver todas as tarefas DONE",
            description = "Returns a list of all DONE tasks for the user within the given date range. | Retorna uma lista de todas as tarefas DONE do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/done/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksDoneInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.DONE)
        ));
    }

    @Operation(
            summary = "View all DONE tasks from a date | Ver todas as tarefas DONE de uma data",
            description = "Returns a list of all DONE tasks for the user for the given date. | Retorna uma lista de todas as tarefas DONE do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/done/{idUser}/{date}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksDoneInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        service.getTasksStatusInDate(idUser, date, Status.DONE)
        ));
    }

    @Operation(
            summary = "View all CANCELED tasks | Ver todas as tarefas CANCELED",
            description = "Returns a list of all CANCELED tasks for the user within the given date range. | Retorna uma lista de todas as tarefas CANCELED do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/canceled/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksCanceledInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.CANCELLED)
        ));
    }

    @Operation(
            summary = "View all CANCELED tasks from a date | Ver todas as tarefas CANCELED de uma data",
            description = "Returns a list of all CANCELED tasks for the user for the given date. | Retorna uma lista de todas as tarefas CANCELED do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/canceled/{idUser}/{date}")
    public CompletableFuture<ResponseEntity<List<Task>>> getTasksCanceledInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                        service.getTasksStatusInDate(idUser, date, Status.CANCELLED)
        ));
    }


}
