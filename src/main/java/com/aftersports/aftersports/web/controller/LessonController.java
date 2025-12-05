package com.aftersports.aftersports.web.controller;

import com.aftersports.aftersports.domain.model.Lesson;
import com.aftersports.aftersports.domain.service.LessonService;
import com.aftersports.aftersports.domain.service.WeatherService;
import com.aftersports.aftersports.infra.external.weather.WeatherSummary;
import com.aftersports.aftersports.web.dto.LessonCreateRequest;
import com.aftersports.aftersports.web.dto.LessonDTO;
import com.aftersports.aftersports.web.dto.LessonUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Lessons")
public class LessonController {

    private final LessonService lessonService;
    private final WeatherService weatherService;

    public LessonController(LessonService lessonService, WeatherService weatherService) {
        this.lessonService = lessonService;
        this.weatherService = weatherService;
    }

    @PostMapping("/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create lesson")
    public LessonDTO create(@Valid @RequestBody LessonCreateRequest request) {
        return lessonService.create(request);
    }

    @GetMapping("/lessons")
    @Operation(summary = "List lessons")
    public List<LessonDTO> list() {
        return lessonService.listAll();
    }

    @GetMapping("/lessons/{id}")
    @Operation(summary = "Get lesson by id")
    public LessonDTO get(@PathVariable Long id) {
        return lessonService.getById(id);
    }

    @PutMapping("/lessons/{id}")
    @Operation(summary = "Update lesson")
    public LessonDTO update(@PathVariable Long id, @Valid @RequestBody LessonUpdateRequest request) {
        return lessonService.update(id, request);
    }

    @DeleteMapping("/lessons/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete lesson")
    public void delete(@PathVariable Long id) {
        lessonService.delete(id);
    }

    @GetMapping("/instructors/{instructorId}/lessons")
    @Operation(summary = "List lessons by instructor")
    public List<LessonDTO> listByInstructor(@PathVariable Long instructorId) {
        return lessonService.findByInstructor(instructorId);
    }

    @GetMapping("/lessons/{id}/weather")
    @Operation(summary = "Get weather forecast for lesson")
    public WeatherSummary weather(@PathVariable Long id) {
        Lesson lesson = lessonService.getEntity(id);
        LocalDate date = lesson.getDateTime().toLocalDate();
        return weatherService.getForecast(lesson.getLat(), lesson.getLon(), date);
    }
}
