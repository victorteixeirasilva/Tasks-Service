package tech.inovasoft.inevolving.ms.tasks.domain.dto.request;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entrada do end-point que adia tarefas de um dia civil.
 * <p>
 * {@code referenceDay} vem em ISO (ano-mês-dia), representando o dia que o usuário vê no calendário
 * no fuso do Brasil (sem componente de hora na requisição).
 * </p>
 *
 * @param idUser        dono das tarefas
 * @param referenceDay  dia civil alvo
 */
public record RequestPostponeTasksForDayDTO(
        UUID idUser,
        LocalDate referenceDay
) {
}
