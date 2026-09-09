package com.studentmanagement.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "student-api")
public record StudentApiProperties(
        @Min(1) @Max(100) int defaultPageSize,
        String defaultSortField,
        @Email String supportEmail) {
}