package com.aftersports.aftersports.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookingCreateRequest(
        @NotNull Long lessonId,
        @NotBlank String studentName,
        @NotBlank @Email String studentEmail
) {
}
