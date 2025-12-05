package com.aftersports.aftersports.web.dto;

import java.time.Instant;

public record BookingDTO(
        Long id,
        Long lessonId,
        String studentName,
        String studentEmail,
        Instant createdAt
) {
}
