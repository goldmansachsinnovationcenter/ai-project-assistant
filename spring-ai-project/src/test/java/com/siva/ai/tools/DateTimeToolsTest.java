package com.siva.ai.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DateTimeToolsTest {

    @InjectMocks
    private DateTimeTools dateTimeTools;

    @BeforeEach
    public void setup() {
        LocaleContextHolder.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
    }

    @Test
    public void testGetCurrentDateTime() throws Exception {
        Method getCurrentDateTimeMethod = DateTimeTools.class.getDeclaredMethod("getCurrentDateTime");
        getCurrentDateTimeMethod.setAccessible(true);
        
        String result = (String) getCurrentDateTimeMethod.invoke(dateTimeTools);
        
        assertNotNull(result);
        assertTrue(result.contains("UTC"));
        
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z\\[UTC\\]"));
    }

    @Test
    public void testSetAlarm() throws Exception {
        Method setAlarmMethod = DateTimeTools.class.getDeclaredMethod("setAlarm", String.class);
        setAlarmMethod.setAccessible(true);
        
        String isoDateTime = "2023-05-15T10:30:45";
        
        assertDoesNotThrow(() -> setAlarmMethod.invoke(dateTimeTools, isoDateTime));
    }

    @Test
    public void testSetAlarmInvalidFormat() throws Exception {
        Method setAlarmMethod = DateTimeTools.class.getDeclaredMethod("setAlarm", String.class);
        setAlarmMethod.setAccessible(true);
        
        String invalidDateTime = "invalid-date-time";
        
        Exception exception = assertThrows(Exception.class, () -> setAlarmMethod.invoke(dateTimeTools, invalidDateTime));
        assertTrue(exception.getCause() instanceof DateTimeParseException);
    }
}
