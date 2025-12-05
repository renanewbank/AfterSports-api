package com.aftersports.aftersports.web.dto;

import jakarta.validation.constraints.NotBlank;

public record InstructorCreateRequest(
        @NotBlank String name,
        @NotBlank String sport,
        String bio
) {
}
