package com.aftersports.aftersports.domain.repo;

import com.aftersports.aftersports.domain.model.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    long countByLessonId(Long lessonId);

    List<Booking> findByLessonId(Long lessonId);

    List<Booking> findByStudentEmailIgnoreCase(String studentEmail);
}
