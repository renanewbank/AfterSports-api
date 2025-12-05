package com.aftersports.aftersports.web.dto;

import java.time.LocalDateTime;

public record LessonDTO(
        Long id,
        Long instructorId,
        String title,
        String description,
        LocalDateTime dateTime,
        Integer durationMinutes,
        Integer capacity,
        Long priceCents,
        Double lat,
        Double lon
) {
}
