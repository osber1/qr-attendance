package com.tracking.attendance.qr.controller;

import com.tracking.attendance.qr.*;
import com.tracking.attendance.qr.mapper.Mapper;
import com.tracking.attendance.qr.model.Event;
import com.tracking.attendance.qr.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;

@RestController
@RequestMapping("api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;
    private final Mapper mapper;

    @PostMapping
    public Collection<EventResponseDTO> create(@RequestBody @Valid EventRequestDTO eventDTO) {
        Periodicity periodicity = eventDTO.getPeriodicity();
        LocalDate endDate = eventDTO.getEndDate();
        LocalDate startDate = eventDTO.getStartDate();
        LocalTime startTime = eventDTO.getStartTime();
        LocalTime endTime = eventDTO.getEndTime();
        Event event = mapper.eventToEntity(eventDTO);
        return mapper.eventToDTOs(service.create(event, startDate, endDate, startTime, endTime, periodicity));
    }

    @GetMapping
    public Collection<EventResponseDTO> getAll() {
        return mapper.eventToDTOs(service.getAll());
    }

    @GetMapping("{id}")
    public EventResponseDTO getOne(@PathVariable Integer id) {
        return mapper.eventToDTO(service.getOne(id));
    }

    @PutMapping()
    public EventResponseDTO update(@RequestBody @Valid EventRequestDTO eventDTO) {
        Event event = mapper.eventToEntity(eventDTO);
        return mapper.eventToDTO(service.update(event));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("group/{id}")
    public Collection<EventResponseDTO> getEventsByGroupId(@PathVariable String id) {
        return mapper.eventToDTOs(service.getEventsByGroupId(id));
    }

    @PutMapping("group/{id}")
    public Collection<EventResponseDTO> editEventsByGroupId(@RequestBody @Valid EventRequestDTO eventDTO) {
        Event event = mapper.eventToEntity(eventDTO);
        return mapper.eventToDTOs(service.editEventsByGroupId(event));
    }

    @DeleteMapping("group/{id}")
    public void deleteEventsByGroupId(@PathVariable String id) {
        service.deleteEventsByGroupId(id);
    }

    @GetMapping("lectorEvents")
    public Collection<EventResponseDTO> getLectorEventsByUserId(@RequestParam String userId) {
        return mapper.eventToDTOs(service.getLectorEventsByUserId(userId));
    }

    @GetMapping("lectorEventsByDate")
    public Collection<EventResponseDTO> getLectorEventsByDateInterval(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo) {
        return mapper.eventToDTOs(service.getLectorEventsByDateInterval(userId, dateFrom, dateTo));
    }

    @GetMapping("studentEvents")
    public Collection<EventResponseDTO> getStudentEventsByUserId(@RequestParam String userId) {
        return mapper.eventToDTOs(service.getStudentEventsByUserId(userId));
    }

    @GetMapping("studentEventsByDate")
    public Collection<EventResponseDTO> getStudentEventsByDateInterval(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo) {
        return mapper.eventToDTOs(service.getStudentEventsByDateInterval(userId, dateFrom, dateTo));
    }

    @GetMapping("lectorEventsByLecture")
    public Collection<EventResponseDTO> getLectorEventsByUserIdAndLectureId(
            @RequestParam String userId,
            @RequestParam int lectureId) {
        return mapper.eventToDTOs(service.getLectorEventsByUserIdAndLectureId(userId, lectureId));
    }

    @GetMapping("studentEventsByLecture")
    public Collection<EventResponseDTO> getStudentEventsByUserIdAndLectureId(
            @RequestParam String userId,
            @RequestParam int lectureId) {
        return mapper.eventToDTOs(service.getStudentEventsByUserIdAndLectureId(userId, lectureId));
    }

    @PostMapping("checkIn")
    public void checkInToEvent(
            @RequestParam String userId,
            @RequestParam String qrId,
            @RequestParam int eventId) {
        service.checkInToEvent(userId, qrId, eventId);
    }

    @PostMapping("checkOut")
    public void checkOutFromEvent(
            @RequestParam String userId,
            @RequestParam String qrId,
            @RequestParam int eventId) {
        service.checkOutFromEvent(userId, qrId, eventId);
    }

    @PostMapping("checkInStudent")
    public void checkInStudentToEvent(@RequestParam String userId, @RequestParam int eventId) {
        service.checkInStudentToEvent(userId, eventId);
    }

    @PostMapping("checkOutStudent")
    public void checkOutStudentFromEvent(@RequestParam String userId, @RequestParam int eventId) {
        service.checkOutStudentFromEvent(userId, eventId);
    }

    @GetMapping("qrCode")
    public QrCodeDTO getQrCode(@RequestParam int eventId) {
        return service.getQrCode(eventId);
    }

    @GetMapping("attendedStudents")
    public Collection<UserWithTimestampDTO> getAttendedStudentsByEventId(@RequestParam int eventId) {
        return service.getAttendedStudentsByEventId(eventId);
    }

    @PostMapping("sharedCheckIn")
    public void checkInStudent(@RequestParam String userId, @RequestParam int eventId, @RequestParam String senderId) {
        service.checkInOtherStudentToEvent(userId, eventId, senderId);
    }

    @PostMapping("sharedCheckOut")
    public void checkOutStudent(@RequestParam String userId, @RequestParam int eventId, @RequestParam String senderId) {
        service.checkOutOtherStudentFromEvent(userId, eventId, senderId);
    }
}
