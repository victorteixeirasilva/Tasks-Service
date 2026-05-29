package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.domain.util.TaskTimestampHelper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class TaskTimestampHelperTest {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    @Test
    void applyOnCreate_setsCreatedAt() {
        // Given
        Task task = new Task();

        // When
        TaskTimestampHelper.applyOnCreate(task);

        // Then
        assertNotNull(task.getCreatedAt());
    }

    @Test
    void applyOnStatusChange_inProgress_setsInProgressAtOnly() {
        // Given
        Task task = new Task();

        // When
        TaskTimestampHelper.applyOnStatusChange(task, Status.IN_PROGRESS);

        // Then
        assertNotNull(task.getInProgressAt());
        assertNull(task.getCompletedAt());
        assertNull(task.getCancelledAt());
    }

    @Test
    void applyOnStatusChange_done_setsCompletedAtOnly() {
        // Given
        Task task = new Task();

        // When
        TaskTimestampHelper.applyOnStatusChange(task, Status.DONE);

        // Then
        assertNotNull(task.getCompletedAt());
        assertNull(task.getInProgressAt());
        assertNull(task.getCancelledAt());
    }

    @Test
    void applyOnStatusChange_cancelled_setsCancelledAtOnly() {
        // Given
        Task task = new Task();

        // When
        TaskTimestampHelper.applyOnStatusChange(task, Status.CANCELLED);

        // Then
        assertNotNull(task.getCancelledAt());
        assertNull(task.getInProgressAt());
        assertNull(task.getCompletedAt());
    }

    @Test
    void applyOnStatusChange_todo_doesNotSetStatusTimestamps() {
        // Given
        Task task = new Task();

        // When
        TaskTimestampHelper.applyOnStatusChange(task, Status.TODO);

        // Then
        assertNull(task.getInProgressAt());
        assertNull(task.getCompletedAt());
        assertNull(task.getCancelledAt());
    }

    @Test
    void applyOnStatusChange_late_doesNotSetStatusTimestamps() {
        // Given
        Task task = new Task();

        // When
        TaskTimestampHelper.applyOnStatusChange(task, Status.LATE);

        // Then
        assertNull(task.getInProgressAt());
        assertNull(task.getCompletedAt());
        assertNull(task.getCancelledAt());
    }

    @Test
    void applyOnStatusChange_retransition_updatesLastEntryTimestamp() throws InterruptedException {
        // Given
        Task task = new Task();

        // When — first IN_PROGRESS
        TaskTimestampHelper.applyOnStatusChange(task, Status.IN_PROGRESS);
        Instant firstInProgress = task.getInProgressAt();
        Thread.sleep(2);

        TaskTimestampHelper.applyOnStatusChange(task, Status.DONE);
        Instant completedAt = task.getCompletedAt();
        Thread.sleep(2);

        TaskTimestampHelper.applyOnStatusChange(task, Status.IN_PROGRESS);
        Instant secondInProgress = task.getInProgressAt();

        // Then
        assertTrue(secondInProgress.isAfter(firstInProgress));
        assertNotNull(completedAt);
        assertTrue(secondInProgress.isAfter(completedAt));
    }

    @Test
    void toOffsetDateTime_nullInstant_returnsNull() {
        // Given / When
        OffsetDateTime result = TaskTimestampHelper.toOffsetDateTime(null, SAO_PAULO);

        // Then
        assertNull(result);
    }

    @Test
    void toOffsetDateTime_withInstant_convertsToUserZone() {
        // Given — May 29 fixed instant (no DST ambiguity in São Paulo: -03:00)
        Instant instant = Instant.parse("2026-05-29T15:00:00Z");

        // When
        OffsetDateTime result = TaskTimestampHelper.toOffsetDateTime(instant, SAO_PAULO);

        // Then
        assertEquals(ZoneOffset.of("-03:00"), result.getOffset());
        assertEquals(2026, result.getYear());
        assertEquals(5, result.getMonthValue());
        assertEquals(29, result.getDayOfMonth());
        assertEquals(12, result.getHour());
    }
}
