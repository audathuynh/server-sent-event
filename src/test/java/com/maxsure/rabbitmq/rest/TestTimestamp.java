package com.maxsure.rabbitmq.rest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Dat Huynh
 * @since 1.0
 */
@Slf4j
class TestTimestamp {

    @Test
    void testTimestamp1() {
        String timestampStr = "2020-08-24T14:03:46Z";
        Assertions.assertDoesNotThrow(() -> {
            TemporalAccessor temporal =
                    DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())
                            .parse(timestampStr);
            Instant instant = Instant.from(temporal);
            Date timestamp = Date.from(instant);
            String timestampStr2 = DateTimeFormatter.ISO_INSTANT
                    .withZone(ZoneId.systemDefault())
                    .format(timestamp.toInstant());
            log.info(timestampStr2);
        });
    }

    @Test
    void testTimestamp2() {
        String timestampStr = "2020-08-24T14:03:46Z";
        Assertions.assertDoesNotThrow(() -> {
            TemporalAccessor temporal = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    .withZone(ZoneId.systemDefault()).parse(timestampStr);
            Instant instant = Instant.from(temporal);
            Date timestamp = Date.from(instant);
            String timestampStr2 = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    .withZone(ZoneId.systemDefault())
                    .format(timestamp.toInstant());
            log.info(timestampStr2);
        });
    }

    @Test
    void testTimestamp3() {
        String timestampStr = "2020-08-24T14:03:46.902615+10:00";
        Assertions.assertDoesNotThrow(
                () -> DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(timestampStr));
    }

    @Test
    void testTimestamp4() {
        Assertions.assertDoesNotThrow(() -> {
            Date timestamp = new Date();
            String timestampStr = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    .withZone(ZoneId.systemDefault())
                    .format(timestamp.toInstant());
            log.info(timestampStr);
        });
    }

    @Test
    void testTimestamp5() {
        String timestampStr = "2020-08-24T14:03:46";
        Assertions.assertThrows(DateTimeParseException.class,
                () -> DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault())
                        .parse(timestampStr));

        Assertions.assertDoesNotThrow(
                () -> DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(timestampStr));
    }

}
