package com.aftersports.aftersports.domain.service;

import com.aftersports.aftersports.domain.model.Instructor;
import com.aftersports.aftersports.domain.repo.InstructorRepository;
import com.aftersports.aftersports.web.dto.InstructorCreateRequest;
import com.aftersports.aftersports.web.dto.InstructorDTO;
import com.aftersports.aftersports.web.error.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public InstructorDTO create(InstructorCreateRequest request) {
        Instructor instructor = new Instructor();
        instructor.setName(request.name());
        instructor.setSport(request.sport());
        instructor.setBio(request.bio());
        Instructor saved = instructorRepository.save(instructor);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<InstructorDTO> listAll() {
        return instructorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstructorDTO getById(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public InstructorDTO update(Long id, InstructorCreateRequest request) {
        Instructor existing = findById(id);
        existing.setName(request.name());
        existing.setSport(request.sport());
        existing.setBio(request.bio());
        return toDTO(instructorRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Instructor existing = findById(id);
        instructorRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public Instructor findById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Instructor not found: " + id));
    }

    private InstructorDTO toDTO(Instructor instructor) {
        return new InstructorDTO(instructor.getId(), instructor.getName(), instructor.getSport(), instructor.getBio());
    }
}
