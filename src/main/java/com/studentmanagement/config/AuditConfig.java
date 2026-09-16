package com.studentmanagement.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    @Bean
    @ConditionalOnProperty(prefix = "student-api.feature.audit-log", name = "enabled", havingValue = "true")
    public AuditLogger auditLogger() {
        return new AuditLogger();
    }
}