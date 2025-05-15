package tech.inovasoft.inevolving.ms.tasks.service.client;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@FeignClient(name = "objectives-service", url = "http://localhost:8080/ms/objectives")
public interface ObjectivesServiceClient {


    @GetMapping("/{idObjective}")
    public ResponseEntity getObjectiveById(@PathVariable UUID idObjective);

}
