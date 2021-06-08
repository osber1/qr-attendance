package com.tracking.attendance.qr.controller;

import com.tracking.attendance.qr.FacultyDTO;
import com.tracking.attendance.qr.mapper.Mapper;
import com.tracking.attendance.qr.model.Faculty;
import com.tracking.attendance.qr.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("api/faculties")
@RequiredArgsConstructor
public class FacultyController {
    private final FacultyService service;
    private final Mapper mapper;

    @PostMapping
    public FacultyDTO create(@RequestBody @Valid FacultyDTO facultyDTO) {
        Faculty faculty = mapper.facultyToEntity(facultyDTO);
        return mapper.facultyToDTO(service.create(faculty));
    }

    @GetMapping
    public Collection<FacultyDTO> getAll() {
        return mapper.facultyToDTOs(service.getAll());
    }

    @GetMapping("{id}")
    public FacultyDTO getOne(@PathVariable Integer id) {
        return mapper.facultyToDTO(service.getOne(id));
    }

    @PutMapping
    public FacultyDTO update(@RequestBody @Valid FacultyDTO facultyDTO) {
        Faculty faculty = mapper.facultyToEntity(facultyDTO);
        return mapper.facultyToDTO(service.update(faculty));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}