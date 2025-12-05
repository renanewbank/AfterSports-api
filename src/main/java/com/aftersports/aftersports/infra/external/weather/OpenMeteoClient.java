package com.aftersports.aftersports.infra.external.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenMeteoClient {

    private final RestClient restClient;

    public OpenMeteoClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
    }

    public WeatherSummary fetchDailyForecast(double lat, double lon, LocalDate date) {
        OpenMeteoResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                        .queryParam("timezone", "UTC")
                        .queryParam("start_date", date)
                        .queryParam("end_date", date)
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);

        if (response == null || response.daily == null || response.daily.time == null || response.daily.time.isEmpty()) {
            return new WeatherSummary(date, null, null, null, "No forecast available");
        }

        Double tempMax = first(response.daily.temperatureMax);
        Double tempMin = first(response.daily.temperatureMin);
        Double precip = first(response.daily.precipitationProbability);
        String summary = buildSummary(tempMax, tempMin, precip);

        return new WeatherSummary(date, tempMax, tempMin, precip, summary);
    }

    private String buildSummary(Double max, Double min, Double precip) {
        String maxStr = max != null ? String.format("max %.1f°C", max) : "max n/d";
        String minStr = min != null ? String.format("min %.1f°C", min) : "min n/d";
        String precipStr = precip != null ? String.format("precip %.0f%%", precip) : "precip n/d";
        return String.join(", ", maxStr, minStr, precipStr);
    }

    private Double first(List<Double> values) {
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    private record OpenMeteoResponse(Daily daily) {
    }

    private record Daily(
            List<String> time,
            @JsonProperty("temperature_2m_max") List<Double> temperatureMax,
            @JsonProperty("temperature_2m_min") List<Double> temperatureMin,
            @JsonProperty("precipitation_probability_max") List<Double> precipitationProbability
    ) {
    }
}
