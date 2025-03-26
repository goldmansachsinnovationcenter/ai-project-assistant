package com.example.springai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class SQLiteDialectConfig {
    // Configuration is handled through application.properties
}
