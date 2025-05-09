package tech.inovasoft.inevolving.ms.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.DaysOfTheWeekDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;

import java.sql.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Tasks", description = "Gerenciador dos end-points de tarefas")
@RestController
@RequestMapping("/ms/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @Operation(
            summary = "Adicionar uma nova tarefa",
            description = "Retorna a tarefa cadastrada."
    )
    @Async("asyncExecutor")
    @PostMapping
    public CompletableFuture<ResponseEntity> addTask(@RequestBody RequestTaskDTO taskDTO) throws DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.addTask(taskDTO)));
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
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.repeatTask(idUser, idTask, daysOfTheWeekDTO, startDate, endDate)));
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
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.updateTask(idUser, idTask, updateTaskDTO)));
    }

    @Operation(
            summary = "Atualizar uma tarefas",
            description = "Retorna as tarefas atualizadas."
    )
    @Async("asyncExecutor")
    @PutMapping("/repeat/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> updateTasksAndTheirFutureRepetitions(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @RequestBody RequestUpdateTaskDTO updateTaskDTO
    ) {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        service.updateTasksAndTheirFutureRepetitions(idUser, idTask, updateTaskDTO)
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
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.updateTaskStatus(idUser, idTask, Status.TODO)));
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
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.updateTaskStatus(idUser, idTask, Status.IN_PROGRESS)));
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
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.updateTaskStatus(idUser, idTask, Status.DONE)));
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
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.updateTaskStatus(idUser, idTask, Status.LATE)));
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
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.updateTaskStatus(idUser, idTask, Status.CANCELLED)));
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
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.deleteTask(idUser, idTask)));
    }

    @Operation(
            summary = "Remover uma tarefas",
            description = "Retorna confirmação que as tarefas foram removidas, e a quantidade de tarefas removidas."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/repeat/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> deleteTasksAndTheirFutureRepetitions(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.deleteTasksAndTheirFutureRepetitions(idUser, idTask)));
    }

    @Operation(
            summary = "Bloquear tarefas com base em um objetivo para manter um historic",
            description = "Retorna confirmação que as tarefas foram bloqueadas, e a quantidade de tarefas removidas, e a quantidade de tarefas bloqueadas."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/lock/{idUser}/{idObjective}")
    public CompletableFuture<ResponseEntity> lockTaskByObjective(
            @PathVariable UUID idUser,
            @PathVariable UUID idObjective
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.lockTaskByObjective(idUser, idObjective)));
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
    ) {
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
    ) {
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
    ) {
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
    ) {
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
    ) {
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
    ) {
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
    ) {
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
    ) {
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
    ) {
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
    ) {
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
    ) {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        service.getTasksStatusInDate(idUser, date, Status.CANCELLED)
                )
        );
    }


}
