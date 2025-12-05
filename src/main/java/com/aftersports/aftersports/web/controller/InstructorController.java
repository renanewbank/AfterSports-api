package com.aftersports.aftersports.web.controller;

import com.aftersports.aftersports.domain.service.InstructorService;
import com.aftersports.aftersports.web.dto.InstructorCreateRequest;
import com.aftersports.aftersports.web.dto.InstructorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/instructors")
@Tag(name = "Instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create instructor")
    public InstructorDTO create(@Valid @RequestBody InstructorCreateRequest request) {
        return instructorService.create(request);
    }

    @GetMapping
    @Operation(summary = "List instructors")
    public List<InstructorDTO> list() {
        return instructorService.listAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get instructor by id")
    public InstructorDTO get(@PathVariable Long id) {
        return instructorService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update instructor")
    public InstructorDTO update(@PathVariable Long id, @Valid @RequestBody InstructorCreateRequest request) {
        return instructorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete instructor")
    public void delete(@PathVariable Long id) {
        instructorService.delete(id);
    }
}
