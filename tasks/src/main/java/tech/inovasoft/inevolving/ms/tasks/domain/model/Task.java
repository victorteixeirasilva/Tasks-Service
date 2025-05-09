package tech.inovasoft.inevolving.ms.tasks.domain.model;

import jakarta.persistence.*;
import lombok.*;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;

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
        if (dto.idObjective().isPresent()) {
            //TODO: verificar no serviço de objetivos se esse objetivo existe.
            this.idObjective = dto.idObjective().get();
        }
        this.idUser = dto.idUser();
    }

    public Task(Task task){
        this.nameTask = task.getNameTask();
        this.descriptionTask = task.getDescriptionTask();
        this.status = task.getStatus();
        this.dateTask = task.getDateTask();
        this.idObjective = task.getIdObjective();
        this.idUser = task.getIdUser();
    }

}
