package com.aftersports.aftersports.web.controller;

import com.aftersports.aftersports.domain.service.BookingService;
import com.aftersports.aftersports.web.dto.BookingCreateRequest;
import com.aftersports.aftersports.web.dto.BookingDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create booking")
    public BookingDTO create(@Valid @RequestBody BookingCreateRequest request) {
        return bookingService.create(request);
    }

    @GetMapping("/lessons/{lessonId}/bookings")
    @Operation(summary = "List bookings by lesson")
    public List<BookingDTO> listByLesson(@PathVariable Long lessonId) {
        return bookingService.listByLesson(lessonId);
    }

    @GetMapping("/bookings/search")
    @Operation(summary = "Search bookings by student email")
    public List<BookingDTO> searchByEmail(@RequestParam String email) {
        return bookingService.listByStudentEmail(email);
    }
}
