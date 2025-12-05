package com.aftersports.aftersports.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record LessonCreateRequest(
        @NotNull Long instructorId,
        @NotBlank String title,
        String description,
        @NotNull LocalDateTime dateTime,
        @NotNull @Positive Integer durationMinutes,
        @NotNull @Positive Integer capacity,
        @NotNull @PositiveOrZero Long priceCents,
        @NotNull Double lat,
        @NotNull Double lon
) {
}
