package tech.inovasoft.inevolving.ms.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateDateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.service.DateTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Tag(name = "Date Task")
@RestController
@RequestMapping("/ms/tasks/date")
public class DateTaskController {

    @Autowired
    private DateTaskService dateTaskService;

    @Autowired
    private TokenService tokenService;

    @Operation(
            summary = "Update a task | Atualizar uma tarefa",
            description = "Returns the updated task. | Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PutMapping("/{token}")
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> updateTask(
            @RequestBody RequestUpdateDateTaskDTO updateDateTaskDTO,
            @PathVariable String token
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
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
                dateTaskService.updateDateTask(updateDateTaskDTO)
        ));
    }


}
