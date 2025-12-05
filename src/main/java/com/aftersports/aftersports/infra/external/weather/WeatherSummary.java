package com.aftersports.aftersports.infra.external.weather;

import java.time.LocalDate;

public record WeatherSummary(
        LocalDate date,
        Double temperatureMax,
        Double temperatureMin,
        Double precipitationProbability,
        String summary
) {
}
