package com.aftersports.aftersports.web.controller;

import com.aftersports.aftersports.domain.service.BookingService;
import com.aftersports.aftersports.web.dto.BookingCreateRequest;
import com.aftersports.aftersports.web.dto.BookingDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    // ALTERADO: agora aceita name (preferencial) e mantém email como legado.
    @GetMapping("/bookings/search")
    @Operation(summary = "Search bookings (prefer 'name'; 'email' kept for legacy)")
    public List<BookingDTO> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email
    ) {
        if (name != null && !name.isBlank()) {
            return bookingService.listByStudentName(name);
        }
        if (email != null && !email.isBlank()) {
            return bookingService.listByStudentEmail(email);
        }
        throw new IllegalArgumentException("Informe 'name' (preferido) ou 'email'.");
    }

    // NOVO: cancelar reserva
    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel booking")
    public void cancel(@PathVariable Long id) {
        bookingService.cancel(id);
    }
}
