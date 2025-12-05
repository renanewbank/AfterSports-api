package com.aftersports.aftersports.domain.service;

import com.aftersports.aftersports.infra.external.weather.OpenMeteoClient;
import com.aftersports.aftersports.infra.external.weather.WeatherSummary;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final OpenMeteoClient openMeteoClient;

    public WeatherService(OpenMeteoClient openMeteoClient) {
        this.openMeteoClient = openMeteoClient;
    }

    public WeatherSummary getForecast(double lat, double lon, LocalDate date) {
        return openMeteoClient.fetchDailyForecast(lat, lon, date);
    }
}
