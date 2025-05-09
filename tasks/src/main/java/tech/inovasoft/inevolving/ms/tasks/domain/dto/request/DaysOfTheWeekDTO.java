package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DaysOfTheWeekDTO {
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
}
