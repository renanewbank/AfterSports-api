package com.aftersports.aftersports.domain.repo;

import com.aftersports.aftersports.domain.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
}
