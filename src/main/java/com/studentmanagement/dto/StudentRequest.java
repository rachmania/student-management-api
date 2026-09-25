package com.studentmanagement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StudentRequest(
        @NotBlank String id,
        @NotBlank String name,
        @Email String email,
        @DecimalMin("0.0") @DecimalMax("4.0") double gpa) {
}