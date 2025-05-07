package tech.inovasoft.inevolving.ms.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Tasks", description = "Gerenciado dos end-points de tarefas")
@RestController
@RequestMapping("/ms/tasks")
public class TaskController {

    @Operation(
            summary = "Adicionar uma nova tarefa",
            description = "Retorna a tarefa cadastrada."
    )
    @Async("asyncExecutor")
    @PostMapping
    public CompletableFuture<ResponseEntity> addTask(@RequestBody RequestTaskDTO dto) {
//      TODO:return CompletableFuture.completedFuture(ResponseEntity.ok(service.addTask(dto)));
        return null;
    }

    @Operation(
            summary = "Repetir uma tarefa",
            description = "Retorna a quantidade de vezes que a tarefa foi repetida"
    )
    @Async("asyncExecutor")
    @PostMapping("repeat/{idUser}/{idTask}")
    public CompletableFuture<ResponseEntity> repeatTask(@PathVariable UUID idUser, @PathVariable UUID idTask) {
//      TODO:return CompletableFuture.completedFuture(ResponseEntity.ok(service.repeatTask(idUser, idTask)));
        return null;
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
            @RequestBody RequestUpdateTaskDTO dto
    ) {
//      TODO:return CompletableFuture.completedFuture(ResponseEntity.ok(service.updateTask(idUser, idTask, dto)));
        return null;
    }

}
