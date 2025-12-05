package com.aftersports.aftersports.web.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record LessonUpdateRequest(
        Long instructorId,
        String title,
        String description,
        LocalDateTime dateTime,
        @Positive Integer durationMinutes,
        @Positive Integer capacity,
        @PositiveOrZero Long priceCents,
        Double lat,
        Double lon
) {
}
