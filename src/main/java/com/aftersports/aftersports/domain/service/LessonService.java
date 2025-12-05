package com.aftersports.aftersports.domain.service;

import com.aftersports.aftersports.domain.model.Instructor;
import com.aftersports.aftersports.domain.model.Lesson;
import com.aftersports.aftersports.domain.repo.BookingRepository;
import com.aftersports.aftersports.domain.repo.LessonRepository;
import com.aftersports.aftersports.web.dto.LessonCreateRequest;
import com.aftersports.aftersports.web.dto.LessonDTO;
import com.aftersports.aftersports.web.dto.LessonUpdateRequest;
import com.aftersports.aftersports.web.error.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final InstructorService instructorService;
    private final BookingRepository bookingRepository;

    public LessonService(LessonRepository lessonRepository,
                         InstructorService instructorService,
                         BookingRepository bookingRepository) {
        this.lessonRepository = lessonRepository;
        this.instructorService = instructorService;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public LessonDTO create(LessonCreateRequest request) {
        Instructor instructor = instructorService.findById(request.instructorId());
        Lesson lesson = new Lesson();
        lesson.setInstructor(instructor);
        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setDateTime(request.dateTime());
        lesson.setDurationMinutes(request.durationMinutes());
        lesson.setCapacity(request.capacity());
        lesson.setPriceCents(request.priceCents());
        lesson.setLat(request.lat());
        lesson.setLon(request.lon());
        Lesson saved = lessonRepository.save(lesson);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> listAll() {
        return lessonRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LessonDTO getById(Long id) {
        return toDTO(getEntity(id));
    }

    @Transactional
    public LessonDTO update(Long id, LessonUpdateRequest request) {
        Lesson lesson = getEntity(id);
        if (request.instructorId() != null) {
            Instructor instructor = instructorService.findById(request.instructorId());
            lesson.setInstructor(instructor);
        }
        if (request.title() != null) {
            lesson.setTitle(request.title());
        }
        if (request.description() != null) {
            lesson.setDescription(request.description());
        }
        if (request.dateTime() != null) {
            lesson.setDateTime(request.dateTime());
        }
        if (request.durationMinutes() != null) {
            lesson.setDurationMinutes(request.durationMinutes());
        }
        if (request.capacity() != null) {
            lesson.setCapacity(request.capacity());
        }
        if (request.priceCents() != null) {
            lesson.setPriceCents(request.priceCents());
        }
        if (request.lat() != null) {
            lesson.setLat(request.lat());
        }
        if (request.lon() != null) {
            lesson.setLon(request.lon());
        }
        return toDTO(lessonRepository.save(lesson));
    }

    @Transactional
    public void delete(Long id) {
        Lesson lesson = getEntity(id);
        lessonRepository.delete(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> findByInstructor(Long instructorId) {
        instructorService.findById(instructorId);
        return lessonRepository.findByInstructorId(instructorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void ensureCapacity(Long lessonId) {
        Lesson lesson = getEntity(lessonId);
        long count = bookingRepository.countByLessonId(lessonId);
        if (count >= lesson.getCapacity()) {
            throw new IllegalArgumentException("Lesson is fully booked");
        }
    }

    @Transactional(readOnly = true)
    public Lesson getEntity(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson not found: " + id));
    }

    private LessonDTO toDTO(Lesson lesson) {
        return new LessonDTO(
                lesson.getId(),
                lesson.getInstructor().getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getDateTime(),
                lesson.getDurationMinutes(),
                lesson.getCapacity(),
                lesson.getPriceCents(),
                lesson.getLat(),
                lesson.getLon()
        );
    }
}
