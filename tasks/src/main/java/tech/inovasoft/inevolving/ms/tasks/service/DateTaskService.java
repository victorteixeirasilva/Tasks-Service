package tech.inovasoft.inevolving.ms.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestPostponeTasksForDayDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateDateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestUpdateTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponsePostponeTasksForDayDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.DataBaseException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.NotFoundException;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.UserWithoutAuthorizationAboutTheTaskException;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.repository.interfaces.TaskRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class DateTaskService {

    @Autowired
    private TaskRepository repository;

    public ResponseTaskDTO updateDateTask(RequestUpdateDateTaskDTO dto) throws UserWithoutAuthorizationAboutTheTaskException, DataBaseException, NotFoundException {
        Task task = repository.findById(dto.idTask(), dto.idUser());

        task.setDateTask(Date.valueOf(dto.dateTask()));

        return new ResponseTaskDTO(repository.saveInDataBase(task));
    }

    /**
     * Fluxo <strong>independente</strong> do {@link #updateDateTask}: usa apenas
     * {@link TaskRepository#findAllByStatusAndDate} e {@link TaskRepository#saveInDataBase} já existentes.
     * <p>
     * Regras: no {@code referenceDay}, tarefas {@link Status#TODO} passam a {@link Status#LATE} e
     * {@code dateTask} += 1 dia; tarefas {@link Status#IN_PROGRESS} mantêm o status e {@code dateTask} += 1 dia.
     * </p>
     */
    public ResponsePostponeTasksForDayDTO postponeTasksForReferenceDay(RequestPostponeTasksForDayDTO dto)
            throws DataBaseException {
        UUID idUser = dto.idUser(); // escopo: apenas tarefas deste usuário
        LocalDate referenceLocal = dto.referenceDay(); // "dia de calendário" enviado pelo front (BR / ISO date)
        Date referenceSql = Date.valueOf(referenceLocal); // alinha com parâmetro Date das queries existentes
        LocalDate nextLocal = referenceLocal.plusDays(1); // dia civil seguinte ao de referência
        Date nextSql = Date.valueOf(nextLocal); // mesma data em java.sql.Date para setDateTask

        // Consulta reutilizada: mesma assinatura já usada em outros serviços, sem alteração.
        List<Task> todoOnDay = repository.findAllByStatusAndDate(idUser, referenceSql, Status.TODO);
        int lateMoved = 0; // contador só para resposta (observabilidade)
        for (Task task : todoOnDay) { // uma persistência por linha afetada
            task.setStatus(Status.LATE); // não iniciada vira atrasada, conforme regra de negócio
            task.setDateTask(nextSql); // empurra compromisso para o próximo dia
            repository.saveInDataBase(task); // método já existente no repositório
            lateMoved++; // incrementa após save bem-sucedido (testes simulam exceção antes se necessário)
        }

        List<Task> inProgressOnDay = repository.findAllByStatusAndDate(idUser, referenceSql, Status.IN_PROGRESS);
        int inProgressMoved = 0;
        for (Task task : inProgressOnDay) {
            task.setDateTask(nextSql); // replaneja sem tocar no status IN_PROGRESS
            repository.saveInDataBase(task);
            inProgressMoved++;
        }

        int total = lateMoved + inProgressMoved; // facilita conferência rápida no cliente / logs
        return new ResponsePostponeTasksForDayDTO(referenceLocal, nextLocal, lateMoved, inProgressMoved, total);
    }
}
