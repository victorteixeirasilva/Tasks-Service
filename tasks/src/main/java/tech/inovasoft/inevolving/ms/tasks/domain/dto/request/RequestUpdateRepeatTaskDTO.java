package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateRepeatTaskDTO{
    public String nameTask;
    public String descriptionTask;
    public UUID idObjective;
    public DaysOfTheWeekDTO daysOfTheWeekDTO = new DaysOfTheWeekDTO();
}
