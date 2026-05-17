package tech.inovasoft.inevolving.ms.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateResponsibleUserDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseResponsibleUserDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.service.ResponsibleUserTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Responsible User Task")
@RestController
@RequestMapping("/ms/tasks/responsible")
public class ResponsibleUserTaskController {

    @Autowired
    private ResponsibleUserTaskService responsibleUserTaskService;

    @Autowired
    private TokenService tokenService;

    @Operation(
            summary = "Update the responsible user of a task | Atualizar o usuário responsável por uma tarefa",
            description = "Returns the task id and the new responsible user id. | Retorna o id da tarefa e o id do novo usuário responsável."
    )
    @Async("asyncExecutor")
    @PutMapping("/{token}")
    public CompletableFuture<ResponseEntity<ResponseResponsibleUserDTO>> updateResponsibleUser(
            @RequestBody RequestUpdateResponsibleUserDTO dto,
            @PathVariable String token
    ) throws DataBaseException, NotFoundException {
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
                responsibleUserTaskService.updateResponsibleUser(dto)
        ));
    }

    @Operation(
            summary = "Get the responsible user of a task | Obter o usuário responsável por uma tarefa",
            description = "Returns the task id and the responsible user id. | Retorna o id da tarefa e o id do usuário responsável."
    )
    @Async("asyncExecutor")
    @GetMapping("/{idUser}/{idTask}/{token}")
    public CompletableFuture<ResponseEntity<ResponseResponsibleUserDTO>> getResponsibleUser(
            @PathVariable UUID idUser,
            @PathVariable UUID idTask,
            @PathVariable String token
    ) throws DataBaseException, NotFoundException {
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
                responsibleUserTaskService.getResponsibleUser(idUser, idTask)
        ));
    }
}
