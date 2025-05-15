package tech.inovasoft.inevolving.ms.tasks.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.service.client.ObjectivesServiceClient;

import java.sql.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nameTask;
    private String descriptionTask;
    private String status;
    private Date dateTask;
    private UUID idObjective;
    private UUID idUser;
    private UUID idParentTask;
    private UUID idOriginalTask;
    private Boolean hasSubtasks;
    private Boolean blockedByObjective;
    private Boolean isCopy;
    private String cancellationReason;

    public Task(RequestTaskDTO dto) {
        this.nameTask = dto.nameTask();
        this.descriptionTask = dto.descriptionTask();
        this.status = Status.TODO;
        this.dateTask = Date.valueOf(dto.dateTask());
        if (dto.idObjective() != null) {
            this.setIdObjective(dto.idObjective());
        }

        this.setIdObjective(dto.idUser());
        this.idUser = dto.idUser();
        this.isCopy = false;
    }

    public Task(RequestTaskDTO dto, UUID idOriginalTask) {
        this.nameTask = dto.nameTask();
        this.descriptionTask = dto.descriptionTask();
        this.status = Status.TODO;
        this.dateTask = Date.valueOf(dto.dateTask());

        if (dto.idObjective() != null) {
            this.setIdObjective(dto.idObjective());
        }
        this.setIdObjective(dto.idUser());

        this.idUser = dto.idUser();
        this.idOriginalTask = idOriginalTask;
        this.isCopy = true;
    }

}
