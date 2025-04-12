package com.example.springai.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @InjectMocks
    private WebConfig webConfig;

    @Mock
    private CorsRegistry corsRegistry;

    @Mock
    private CorsRegistration corsRegistration;

    @Captor
    private ArgumentCaptor<String[]> stringArrayCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<Boolean> booleanCaptor;

    @BeforeEach
    void setUp() {
        lenient().when(corsRegistry.addMapping(anyString())).thenReturn(corsRegistration);
        lenient().when(corsRegistration.allowedOrigins(any(String[].class))).thenReturn(corsRegistration);
        lenient().when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        lenient().when(corsRegistration.allowedHeaders(anyString())).thenReturn(corsRegistration);
        lenient().when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
    }

    @Test
    void addCorsMappings_ConfiguresRegistryCorrectly() {
        webConfig.addCorsMappings(corsRegistry);

        verify(corsRegistry).addMapping("/**");

        verify(corsRegistration).allowedOrigins(stringArrayCaptor.capture());
        List<String> expectedOrigins = Arrays.asList("http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003");
        assertArrayEquals(expectedOrigins.toArray(new String[0]), stringArrayCaptor.getValue());

        verify(corsRegistration).allowedMethods(stringArrayCaptor.capture());
        List<String> expectedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertArrayEquals(expectedMethods.toArray(new String[0]), stringArrayCaptor.getValue());

        verify(corsRegistration).allowedHeaders(stringCaptor.capture());
        assertEquals("*", stringCaptor.getValue());

        verify(corsRegistration).allowCredentials(booleanCaptor.capture());
        assertTrue(booleanCaptor.getValue());
    }

    @Test
    void corsFilter_BeanCreation_ConfiguresFilterCorrectly() {
        CorsFilter corsFilter = webConfig.corsFilter();
        assertNotNull(corsFilter, "CorsFilter bean should be created successfully.");

    }
}
