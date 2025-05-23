package tech.inovasoft.inevolving.ms.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateRepeatTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.*;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.service.RecurringTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Tag(name = "Tasks", description = "Gerenciador dos end-points de tarefas")
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
            summary = "Adicionar uma nova tarefa",
            description = "Retorna a tarefa cadastrada."
    )
    @Async("asyncExecutor")
    @PostMapping
    public CompletableFuture<ResponseEntity> addTask(@RequestBody RequestTaskDTO taskDTO) throws DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(simpleTaskService.addTask(taskDTO)));
    }

    @Operation(
            summary = "Repetir uma tarefa",
            description = "Retorna a quantidade de vezes que a tarefa foi repetida"
    )
    @Async("asyncExecutor")
    @PostMapping("/repeat/{idUser}/{idTask}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity> repeatTask(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable Date startDate,
            @PathVariable Date endDate,
            @RequestBody DaysOfTheWeekDTO daysOfTheWeekDTO
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(recurringTaskService.addTasks(idUser, idTask, daysOfTheWeekDTO, startDate, endDate)));
    }

    @Operation(
            summary = "Atualizar uma tarefa",
            description = "Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PutMapping("/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> updateTask(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @RequestBody RequestUpdateTaskDTO updateTaskDTO
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(simpleTaskService.updateTask(idUser, idTask, updateTaskDTO)));
    }

    @Operation(
            summary = "Atualizar uma tarefas",
            description = "Retorna as tarefas atualizadas."
    )
    @Async("asyncExecutor")
    @PutMapping("/repeat/{idUser}/{idTask}/{endDate}")
    public CompletableFuture<ResponseEntity> updateTasksAndTheirFutureRepetitions(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable Date endDate,
            @RequestBody RequestUpdateRepeatTaskDTO updateTaskDTO
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        recurringTaskService.updateTasks(idUser, idTask, endDate, updateTaskDTO)
                )
        );
    }

    @Operation(
            summary = "Atualizar o status para To Do",
            description = "Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/todo/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> updateTaskStatusToDo(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(simpleTaskService.updateTaskStatus(idUser, idTask, Status.TODO)));
    }

    @Operation(
            summary = "Atualizar o status para In Progress",
            description = "Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/progress/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> updateTaskStatusInProgress(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(simpleTaskService.updateTaskStatus(idUser, idTask, Status.IN_PROGRESS)));
    }

    @Operation(
            summary = "Atualizar o status para Done",
            description = "Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/done/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> updateTaskStatusDone(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(simpleTaskService.updateTaskStatus(idUser, idTask, Status.DONE)));
    }

    @Operation(
            summary = "Atualizar o status para Late",
            description = "Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/late/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> updateTaskStatusLate(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(simpleTaskService.updateTaskStatus(idUser, idTask, Status.LATE)));
    }

    @Operation(
            summary = "Atualizar o status para Canceled",
            description = "Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PatchMapping("/status/canceled/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> updateTaskStatusCanceled(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(simpleTaskService.updateTaskStatus(idUser, idTask, Status.CANCELLED)));
    }

    @Operation(
            summary = "Remover uma tarefa",
            description = "Retorna confirmação que a tarefa foi removida."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> deleteTask(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(simpleTaskService.deleteTask(idUser, idTask)));
    }

    @Operation(
            summary = "Remover uma tarefas",
            description = "Retorna confirmação que as tarefas foram removidas, e a quantidade de tarefas removidas."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/repeat/{idUser}/{idTask}/{date}")
    public CompletableFuture<ResponseEntity> deleteTasksAndTheirFutureRepetitions(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable Date date
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(recurringTaskService.deleteTasks(idUser, idTask, date)));
    }

    @Operation(
            summary = "Bloquear tarefas com base em um objetivo para manter um historic",
            description = "Retorna confirmação que as tarefas foram bloqueadas, e a quantidade de tarefas removidas, e a quantidade de tarefas bloqueadas."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/lock/{completionDate}/{idUser}/{idObjective}")
    public CompletableFuture<ResponseEntity> lockTaskByObjective(
            @PathVariable Date completionDate,
            @PathVariable UUID idUser,
            @PathVariable UUID idObjective
    ) throws UserWithoutAuthorizationAboutTheTaskException, NotFoundException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.lockTaskByObjective(idUser, idObjective, completionDate)));
    }

    @Operation(
            summary = "Ver todas as tarefas",
            description = "Retorna uma lista de todas as tarefas do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity> getTasksInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksInDateRangeException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.getTasksInDateRange(idUser, startDate, endDate)));
    }

    @Operation(
            summary = "Ver todas as tarefas de uma data",
            description = "Retorna uma lista de todas as tarefas do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/{idUser}/{date}")
    public CompletableFuture<ResponseEntity> getTasksInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksInDateException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.getTasksInDate(idUser, date)));
    }

    @Operation(
            summary = "Ver todas as tarefas atrasadas",
            description = "Retorna uma lista de todas as tarefas atrasadas do usuário."
    )
    @Async("asyncExecutor")
    @GetMapping("/late/{idUser}")
    public CompletableFuture<ResponseEntity> getTasksLate(
            @PathVariable UUID idUser
    ) throws NotFoundTasksWithStatusLateException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.getTasksLate(idUser)));
    }

    @Operation(
            summary = "Ver todas as tarefas TODO",
            description = "Retorna uma lista de todas as tarefas TODO do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/todo/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity> getTasksToDoInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.TODO)));
    }

    @Operation(
            summary = "Ver todas as tarefas TODO de uma data",
            description = "Retorna uma lista de todas as tarefas TODO do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/todo/{idUser}/{date}")
    public CompletableFuture<ResponseEntity> getTasksToDoInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.getTasksStatusInDate(idUser, date, Status.TODO)));
    }

    @Operation(
            summary = "Ver todas as tarefas IN PROGRESS",
            description = "Retorna uma lista de todas as tarefas IN PROGRESS do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/progress/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity> getTasksInProgressInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.IN_PROGRESS)
                )
        );
    }

    @Operation(
            summary = "Ver todas as tarefas IN PROGRESS de uma data",
            description = "Retorna uma lista de todas as tarefas IN PROGRESS do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/progress/{idUser}/{date}")
    public CompletableFuture<ResponseEntity> getTasksInProgressInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        service.getTasksStatusInDate(idUser, date, Status.IN_PROGRESS)
                )
        );
    }

    @Operation(
            summary = "Ver todas as tarefas DONE",
            description = "Retorna uma lista de todas as tarefas DONE do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/done/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity> getTasksDoneInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.DONE)
                )
        );
    }

    @Operation(
            summary = "Ver todas as tarefas DONE de uma data",
            description = "Retorna uma lista de todas as tarefas DONE do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/done/{idUser}/{date}")
    public CompletableFuture<ResponseEntity> getTasksDoneInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        service.getTasksStatusInDate(idUser, date, Status.DONE)
                )
        );
    }

    @Operation(
            summary = "Ver todas as tarefas CANCELED",
            description = "Retorna uma lista de todas as tarefas CANCELED do usuário dentro do intervalo de datas fornecido."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/canceled/{idUser}/{startDate}/{endDate}")
    public CompletableFuture<ResponseEntity> getTasksCanceledInDateRange(
            @PathVariable UUID idUser,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        service.getTasksStatusInDateRange(idUser, startDate, endDate, Status.CANCELLED)
                )
        );
    }

    @Operation(
            summary = "Ver todas as tarefas CANCELED de uma data",
            description = "Retorna uma lista de todas as tarefas CANCELED do usuário para a data informada."
    )
    @Async("asyncExecutor")
    @GetMapping("/status/canceled/{idUser}/{date}")
    public CompletableFuture<ResponseEntity> getTasksCanceledInDate(
            @PathVariable UUID idUser,
            @PathVariable Date date
    ) throws NotFoundTasksWithStatusException, DataBaseException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        service.getTasksStatusInDate(idUser, date, Status.CANCELLED)
                )
        );
    }


}
