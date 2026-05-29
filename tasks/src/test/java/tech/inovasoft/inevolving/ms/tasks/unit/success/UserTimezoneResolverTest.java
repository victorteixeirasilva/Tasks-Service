package tech.inovasoft.inevolving.ms.tasks.unit.success;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.inovasoft.inevolving.ms.tasks.domain.exception.InvalidTimezoneException;
import tech.inovasoft.inevolving.ms.tasks.domain.util.UserTimezoneResolver;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTimezoneResolverTest {

    @Test
    void resolve_nullHeader_usesDefaultTimezone() throws InvalidTimezoneException {
        // Given / When
        ZoneId zone = UserTimezoneResolver.resolve(null);

        // Then
        assertEquals(ZoneId.of(UserTimezoneResolver.DEFAULT_TIMEZONE), zone);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void resolve_blankHeader_usesDefaultTimezone(String header) throws InvalidTimezoneException {
        // Given / When
        ZoneId zone = UserTimezoneResolver.resolve(header);

        // Then
        assertEquals(ZoneId.of(UserTimezoneResolver.DEFAULT_TIMEZONE), zone);
    }

    @Test
    void resolve_validIana_returnsZoneId() throws InvalidTimezoneException {
        // Given / When
        ZoneId saoPaulo = UserTimezoneResolver.resolve("America/Sao_Paulo");
        ZoneId utc = UserTimezoneResolver.resolve("UTC");

        // Then
        assertEquals(ZoneId.of("America/Sao_Paulo"), saoPaulo);
        assertEquals(ZoneId.of("UTC"), utc);
    }

    @Test
    void resolve_trimmedHeader_returnsZoneId() throws InvalidTimezoneException {
        // Given / When
        ZoneId zone = UserTimezoneResolver.resolve("  Europe/Lisbon  ");

        // Then
        assertEquals(ZoneId.of("Europe/Lisbon"), zone);
    }

    @ParameterizedTest
    @ValueSource(strings = {"valid-token", "Not/A/Zone", "Invalid/Timezone/Id"})
    void resolve_invalidIana_throwsInvalidTimezoneException(String invalid) {
        // Given / When / Then
        InvalidTimezoneException ex = assertThrows(
                InvalidTimezoneException.class,
                () -> UserTimezoneResolver.resolve(invalid)
        );
        assertEquals("Invalid timezone: " + invalid.trim(), ex.getMessage());
    }
}
