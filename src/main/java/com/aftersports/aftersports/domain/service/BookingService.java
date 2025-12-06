package com.aftersports.aftersports.domain.service;

import com.aftersports.aftersports.domain.model.Booking;
import com.aftersports.aftersports.domain.model.Lesson;
import com.aftersports.aftersports.domain.repo.BookingRepository;
import com.aftersports.aftersports.web.dto.BookingCreateRequest;
import com.aftersports.aftersports.web.dto.BookingDTO;
import com.aftersports.aftersports.web.error.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final LessonService lessonService;

    public BookingService(BookingRepository bookingRepository, LessonService lessonService) {
        this.bookingRepository = bookingRepository;
        this.lessonService = lessonService;
    }

    @Transactional
    public BookingDTO create(BookingCreateRequest request) {
        lessonService.ensureCapacity(request.lessonId());
        Lesson lesson = lessonService.getEntity(request.lessonId());
        Booking booking = new Booking();
        booking.setLesson(lesson);
        booking.setStudentName(request.studentName());
        booking.setStudentEmail(request.studentEmail());
        Booking saved = bookingRepository.save(booking);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> listByLesson(Long lessonId) {
        lessonService.getEntity(lessonId);
        return bookingRepository.findByLessonId(lessonId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> listByStudentEmail(String email) {
        return bookingRepository.findByStudentEmailIgnoreCase(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // NOVO: busca por NOME
    @Transactional(readOnly = true)
    public List<BookingDTO> listByStudentName(String name) {
        return bookingRepository.findByStudentNameContainingIgnoreCase(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // NOVO: cancelar (deletar) uma reserva
    @Transactional
    public void cancel(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        bookingRepository.delete(booking);
    }

    private BookingDTO toDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getLesson().getId(),
                booking.getStudentName(),
                booking.getStudentEmail(),
                booking.getCreatedAt()
        );
    }
}
