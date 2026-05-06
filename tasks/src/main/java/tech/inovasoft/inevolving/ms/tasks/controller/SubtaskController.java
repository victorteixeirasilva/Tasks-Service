package tech.inovasoft.inevolving.ms.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseMessageDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.service.SubtaskService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Subtasks", description = "Subtask endpoint manager | Gerenciador dos end-points de subtarefas")
@RestController
@RequestMapping("/ms/tasks/subtask")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;

    @Autowired
    private TokenService tokenService;

    @Operation(
            summary = "Create a subtask | Criar uma subtarefa",
            description = "Creates a subtask linked to a parent task. Inherits the parent's objective. | Cria uma subtarefa vinculada a uma tarefa pai. Herda o objetivo da tarefa pai."
    )
    @Async("asyncExecutor")
    @PostMapping("/{token}")
    public CompletableFuture<ResponseEntity<ResponseSubtaskDTO>> createSubtask(
            @RequestBody RequestSubtaskDTO dto,
            @PathVariable String token
    ) throws DataBaseException, NotFoundException, UserWithoutAuthorizationAboutTheTaskException {
        TokenValidateResponse tokenValidateResponse = null;

        try {
            tokenValidateResponse = tokenService.validateToken(token);
            if (tokenValidateResponse == null) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid token")) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        }

        return CompletableFuture.completedFuture(ResponseEntity.ok(
                subtaskService.createSubtask(dto)
        ));
    }

    @Operation(
            summary = "Get subtasks of a parent task | Obter subtarefas de uma tarefa pai",
            description = "Returns all subtasks linked to the given parent task. Returns empty list if none. | Retorna todas as subtarefas vinculadas à tarefa pai. Retorna lista vazia se não houver."
    )
    @Async("asyncExecutor")
    @GetMapping("/{idUser}/{idParentTask}/{token}")
    public CompletableFuture<ResponseEntity<List<Task>>> getSubtasks(
            @PathVariable UUID idUser,
            @PathVariable UUID idParentTask,
            @PathVariable String token
    ) throws DataBaseException, NotFoundException, UserWithoutAuthorizationAboutTheTaskException {
        TokenValidateResponse tokenValidateResponse = null;

        try {
            tokenValidateResponse = tokenService.validateToken(token);
            if (tokenValidateResponse == null) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid token")) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        }

        return CompletableFuture.completedFuture(ResponseEntity.ok(
                subtaskService.getSubtasks(idUser, idParentTask)
        ));
    }

    @Operation(
            summary = "Promote subtask to parent task | Promover subtarefa para tarefa pai",
            description = "Removes the idParentTask from the subtask, making it an independent task. | Remove o idParentTask da subtarefa, tornando-a uma tarefa independente."
    )
    @Async("asyncExecutor")
    @PutMapping("/promote/{idUser}/{idTask}/{token}")
    public CompletableFuture<ResponseEntity<ResponseSubtaskDTO>> promoteToParent(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable String token
    ) throws DataBaseException, NotFoundException, UserWithoutAuthorizationAboutTheTaskException {
        TokenValidateResponse tokenValidateResponse = null;

        try {
            tokenValidateResponse = tokenService.validateToken(token);
            if (tokenValidateResponse == null) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid token")) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        }

        return CompletableFuture.completedFuture(ResponseEntity.ok(
                subtaskService.promoteToParent(idUser, idTask)
        ));
    }

    @Operation(
            summary = "Delete a subtask | Deletar uma subtarefa",
            description = "Deletes a subtask and updates the parent's hasSubtasks if it was the last one. | Deleta uma subtarefa e atualiza o hasSubtasks da tarefa pai caso seja a última."
    )
    @Async("asyncExecutor")
    @DeleteMapping("/{idUser}/{idTask}/{token}")
    public CompletableFuture<ResponseEntity<ResponseMessageDTO>> deleteSubtask(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable String token
    ) throws DataBaseException, NotFoundException, UserWithoutAuthorizationAboutTheTaskException {
        TokenValidateResponse tokenValidateResponse = null;

        try {
            tokenValidateResponse = tokenService.validateToken(token);
            if (tokenValidateResponse == null) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid token")) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        }

        return CompletableFuture.completedFuture(ResponseEntity.ok(
                subtaskService.deleteSubtask(idUser, idTask)
        ));
    }
}
