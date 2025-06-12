package com.example.springai;

import com.example.springai.config.JiraConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(JiraConfigProperties.class)
@ComponentScan(basePackages = {
    "com.example.springai",
    "com.example.springai.controller",
    "com.example.springai.service",
    "com.example.springai.config",
    "com.example.springai.mcp",
    "com.example.springai.model",
    "com.example.springai.tools"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
