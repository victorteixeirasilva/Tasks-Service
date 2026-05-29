package tech.inovasoft.inevolving.ms.tasks.domain.util;

import tech.inovasoft.inevolving.ms.tasks.domain.exception.InvalidTimezoneException;

import java.time.DateTimeException;
import java.time.ZoneId;

public final class UserTimezoneResolver {

    public static final String HEADER_NAME = "X-User-Timezone";
    public static final String DEFAULT_TIMEZONE = "America/Sao_Paulo";

    private UserTimezoneResolver() {
    }

    public static ZoneId resolve(String timezoneHeader) throws InvalidTimezoneException {
        String timezone = timezoneHeader;
        if (timezone == null || timezone.isBlank()) {
            timezone = DEFAULT_TIMEZONE;
        }
        try {
            return ZoneId.of(timezone.trim());
        } catch (DateTimeException e) {
            throw new InvalidTimezoneException(timezone.trim());
        }
    }
}
