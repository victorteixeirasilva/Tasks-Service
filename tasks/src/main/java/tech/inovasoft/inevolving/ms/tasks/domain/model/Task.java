package tech.inovasoft.inevolving.ms.tasks.domain.model;

import jakarta.persistence.*;
import lombok.*;

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

}
