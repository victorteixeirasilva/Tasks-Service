package tech.inovasoft.inevolving.ms.tasks.domain.util;

import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class TaskTimestampHelper {

    private TaskTimestampHelper() {
    }

    public static void applyOnCreate(Task task) {
        task.setCreatedAt(Instant.now());
    }

    public static void applyOnStatusChange(Task task, String newStatus) {
        Instant now = Instant.now();
        if (Status.IN_PROGRESS.equals(newStatus)) {
            task.setInProgressAt(now);
        } else if (Status.DONE.equals(newStatus)) {
            task.setCompletedAt(now);
        } else if (Status.CANCELLED.equals(newStatus)) {
            task.setCancelledAt(now);
        }
    }

    public static OffsetDateTime toOffsetDateTime(Instant instant, ZoneId zoneId) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(zoneId).toOffsetDateTime();
    }
}
