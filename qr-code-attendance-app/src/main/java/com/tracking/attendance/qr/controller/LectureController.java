package com.tracking.attendance.qr.controller;

import com.tracking.attendance.qr.LectureDTO;
import com.tracking.attendance.qr.mapper.Mapper;
import com.tracking.attendance.qr.model.Lecture;
import com.tracking.attendance.qr.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("api/lectures")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService service;
    private final Mapper mapper;

    @PostMapping
    public LectureDTO create(@RequestBody @Valid LectureDTO lectureDTO) {
        Lecture lecture = mapper.lectureToEntity(lectureDTO);
        return mapper.lectureToDTO(service.create(lecture));
    }

    @GetMapping
    public Collection<LectureDTO> getAll() {
        return mapper.lectureToDTOs(service.getAll());
    }

    @GetMapping("{id}")
    public LectureDTO getOne(@PathVariable Integer id) {
        return mapper.lectureToDTO(service.getOne(id));
    }

    @PutMapping
    public LectureDTO update(@RequestBody @Valid LectureDTO lectureDTO) {
        Lecture lecture = mapper.lectureToEntity(lectureDTO);
        return mapper.lectureToDTO(service.update(lecture));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("attendableLectures/{id}")
    public Collection<LectureDTO> getAttendableLecturesByUserId(@PathVariable String id) {
        Collection<LectureDTO> lectureDTOS = mapper.lectureToDTOs(service.getAttendableLectures(id));
        lectureDTOS.parallelStream().forEach(l -> l.setAttendance(service.getLectureAttendance(l.getId(), id)));
        return lectureDTOS;
    }

    @GetMapping("ledLectures/{id}")
    public Collection<LectureDTO> getLedLecturesByUserId(@PathVariable String id) {
        return mapper.lectureToDTOs(service.getLedLectures(id));
    }
}