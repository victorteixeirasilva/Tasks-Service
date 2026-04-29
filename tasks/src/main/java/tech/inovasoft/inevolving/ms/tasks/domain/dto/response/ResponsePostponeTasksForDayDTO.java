package tech.inovasoft.inevolving.ms.tasks.domain.dto.response;

import java.time.LocalDate;

/**
 * Resumo do adiamento: quantas tarefas foram atualizadas e para qual dia foram movidas.
 *
 * @param referenceDay              dia usado na filtragem
 * @param nextDay                   data D+1 aplicada nas tarefas afetadas
 * @param todosMarkedLateAndMoved   TODO do dia → LATE e data +1
 * @param inProgressDatesMoved      IN_PROGRESS do dia → só data +1
 * @param totalTasksUpdated         soma dos dois grupos
 */
public record ResponsePostponeTasksForDayDTO(
        LocalDate referenceDay,
        LocalDate nextDay,
        int todosMarkedLateAndMoved,
        int inProgressDatesMoved,
        int totalTasksUpdated
) {
}
