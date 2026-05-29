package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseSubtaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.TaskViewDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.sql.Date;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTimestampsDtoTest {

    private static final ZoneId USER_ZONE = ZoneId.of("America/Sao_Paulo");
    private static final Instant CREATED = Instant.parse("2026-05-29T10:00:00Z");
    private static final Instant IN_PROGRESS = Instant.parse("2026-05-29T11:00:00Z");
    private static final Instant COMPLETED = Instant.parse("2026-05-29T12:00:00Z");
    private static final Instant CANCELLED = Instant.parse("2026-05-29T13:00:00Z");

    @Test
    void responseTaskDTO_legacyTask_nullTimestamps() {
        // Given
        Task task = buildTask(null, null, null, null);

        // When
        ResponseTaskDTO dto = new ResponseTaskDTO(task, USER_ZONE);

        // Then
        assertNull(dto.createdAt());
        assertNull(dto.inProgressAt());
        assertNull(dto.completedAt());
        assertNull(dto.cancelledAt());
        assertEquals(task.getId(), dto.id());
        assertEquals(Status.TODO, dto.status());
    }

    @Test
    void responseTaskDTO_withTimestamps_convertsToUserZone() {
        // Given
        Task task = buildTask(CREATED, IN_PROGRESS, COMPLETED, CANCELLED);

        // When
        ResponseTaskDTO dto = new ResponseTaskDTO(task, USER_ZONE);

        // Then
        assertNotNull(dto.createdAt());
        assertNotNull(dto.inProgressAt());
        assertNotNull(dto.completedAt());
        assertNotNull(dto.cancelledAt());
        assertEquals(OffsetDateTime.ofInstant(CREATED, USER_ZONE), dto.createdAt());
        assertEquals(task.getIdObjective(), dto.idObjective());
        assertEquals(task.getIdUser(), dto.idUser());
    }

    @Test
    void responseSubtaskDTO_mapsParentAndTimestamps() {
        // Given
        UUID idParent = UUID.randomUUID();
        Task task = buildTask(CREATED, null, null, null);
        task.setIdParentTask(idParent);

        // When
        ResponseSubtaskDTO dto = new ResponseSubtaskDTO(task, USER_ZONE);

        // Then
        assertEquals(idParent, dto.idParentTask());
        assertEquals(OffsetDateTime.ofInstant(CREATED, USER_ZONE), dto.createdAt());
        assertNull(dto.inProgressAt());
    }

    @Test
    void taskViewDTO_preservesEntityFieldsAndTimestamps() {
        // Given
        UUID idResponsible = UUID.randomUUID();
        Task task = buildTask(CREATED, IN_PROGRESS, null, null);
        task.setIdResponsibleUser(idResponsible);
        task.setHasSubtasks(true);
        task.setBlockedByObjective(false);

        // When
        TaskViewDTO dto = new TaskViewDTO(task, USER_ZONE);

        // Then
        assertEquals(task.getId(), dto.id());
        assertEquals(task.getNameTask(), dto.nameTask());
        assertEquals(idResponsible, dto.idResponsibleUser());
        assertTrue(dto.hasSubtasks());
        assertEquals(OffsetDateTime.ofInstant(CREATED, USER_ZONE), dto.createdAt());
        assertEquals(OffsetDateTime.ofInstant(IN_PROGRESS, USER_ZONE), dto.inProgressAt());
    }

    @Test
    void taskViewDTO_fromList_preservesOrderAndSize() {
        // Given
        Task t1 = buildTask(CREATED, null, null, null);
        Task t2 = buildTask(CREATED, null, null, null);

        // When
        List<TaskViewDTO> list = TaskViewDTO.fromList(List.of(t1, t2), USER_ZONE);

        // Then
        assertEquals(2, list.size());
        assertEquals(t1.getId(), list.get(0).id());
        assertEquals(t2.getId(), list.get(1).id());
    }

    private static Task buildTask(Instant created, Instant inProgress, Instant completed, Instant cancelled) {
        UUID id = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        return new Task(
                id,
                "Task",
                "Desc",
                Status.TODO,
                Date.valueOf("2026-05-29"),
                idObjective,
                idUser,
                null,
                null,
                false,
                false,
                false,
                null,
                null,
                created,
                inProgress,
                completed,
                cancelled
        );
    }
}
