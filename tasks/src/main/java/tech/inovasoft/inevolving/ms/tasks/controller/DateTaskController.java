package tech.inovasoft.inevolving.ms.tasks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Operation(
            summary = "Update a task | Atualizar uma tarefa",
            description = "Returns the updated task. | Retorna a tarefa atualizada."
    )
    @Async("asyncExecutor")
    @PutMapping()
    public CompletableFuture<ResponseEntity<ResponseTaskDTO>> updateTask(
            @RequestBody RequestUpdateDateTaskDTO updateDateTaskDTO
    ) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException, ExecutionException, InterruptedException, TimeoutException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                dateTaskService.updateDateTask(updateDateTaskDTO)
        ));
    }


}
