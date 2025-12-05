package com.aftersports.aftersports.domain.repo;

import com.aftersports.aftersports.domain.model.Lesson;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByInstructorId(Long instructorId);
}
