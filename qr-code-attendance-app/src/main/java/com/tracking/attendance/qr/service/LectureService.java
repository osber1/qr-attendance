package com.tracking.attendance.qr.service;

import com.tracking.attendance.qr.AttendanceDTO;
import com.tracking.attendance.qr.exception.NotFoundException;
import com.tracking.attendance.qr.model.Event;
import com.tracking.attendance.qr.model.Lecture;
import com.tracking.attendance.qr.model.User;
import com.tracking.attendance.qr.repository.EventRepository;
import com.tracking.attendance.qr.repository.LectureRepository;
import com.tracking.attendance.qr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.tracking.attendance.qr.util.Util.getVilniusTime;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureService {
    public static final String LECTURE_NOT_FOUND = "Lecture not found with id: ";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private final LectureRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventService eventService;

    public Lecture create(Lecture lecture) {
        repository.save(lecture);
        if (lecture.getAssignedStudents() != null) {
            clearUsers(lecture);
            setStudents(lecture);
        }
        if (lecture.getAssignedLectors() != null) {
            setLectors(lecture);
        }
        return repository.findById(lecture.getId())
                .orElseThrow(() -> new NotFoundException(LECTURE_NOT_FOUND + lecture.getId()));
    }

    @Transactional(readOnly = true)
    public Collection<Lecture> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Lecture getOne(Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(LECTURE_NOT_FOUND + id));
    }

    public Lecture update(Lecture lecture) {
        if (!repository.existsById(lecture.getId())) {
            throw new NotFoundException(LECTURE_NOT_FOUND + lecture.getId());
        }
        if (lecture.getAssignedStudents() != null) {
            clearUsers(lecture);
            setStudents(lecture);
        }
        if (lecture.getAssignedLectors() != null) {
            setLectors(lecture);
        }
        return repository.save(lecture);
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(LECTURE_NOT_FOUND + id);
        }
        eventRepository.deleteAll(getOne(id).getEvents());
        repository.deleteById(id);
    }

    private void clearUsers(Lecture lecture) {
        List<User> allUsers = userRepository.findAll()
                .stream()
                .peek(u -> {
                    Set<Lecture> attendableLectures = u.getAttendableLectures();
                    attendableLectures.removeIf(l -> l.getId() == lecture.getId());
                }).peek(u -> {
                    Set<Lecture> ledLectures = u.getLedLectures();
                    ledLectures.removeIf(l -> l.getId() == lecture.getId());
                }).collect(Collectors.toList());
        userRepository.saveAll(allUsers);
    }

    private void setStudents(Lecture lecture) {
        Set<User> students = lecture.getAssignedStudents()
                .stream()
                .map(u -> userRepository.findById(u.getId()).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + u.getId())))
                .collect(Collectors.toSet());
        students.forEach(s -> s.getAttendableLectures().add(lecture));

        List<Event> events = eventRepository.findAll().stream()
                .filter(e -> e.getLecture().getId() == lecture.getId())
                .filter(e -> e.getStartDate().isAfter(getVilniusTime()))
                .collect(Collectors.toList());
        events.forEach(e -> {
            e.setAttendableStudents(students);
            eventService.update(e);
        });
        userRepository.saveAll(students);
    }

    private void setLectors(Lecture lecture) {
        List<User> lectors = lecture.getAssignedLectors()
                .stream()
                .map(u -> userRepository.findById(u.getId()).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + u.getId())))
                .collect(Collectors.toList());

        lectors.forEach(s -> userRepository.findById(s.getId()).get().getLedLectures().add(lecture));
        userRepository.saveAll(lectors);
    }

    public Collection<Lecture> getAttendableLectures(String id) {
        return getUser(id).getAttendableLectures();
    }

    public Collection<Lecture> getLedLectures(String id) {
        return getUser(id).getLedLectures();
    }

    private User getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + id));
    }

    public AttendanceDTO getLectureAttendance(int id, String userId) {
        Lecture lecture = getOne(id);
        int totalEvents = lecture.getEvents().size();
        int leftEvents = (int) lecture.getEvents()
                .parallelStream()
                .filter(s -> s.getStartDate().isAfter(getVilniusTime()))
                .count();
        AtomicInteger completedEventsAtomic = new AtomicInteger();
        lecture.getEvents()
                .parallelStream()
                .forEach(e -> completedEventsAtomic.addAndGet((int) e.getAttendedStudents()
                        .parallelStream()
                        .filter(s -> s.getStudentId().equals(userId) && s.isCompleted())
                        .count()));
        int completedEvents = completedEventsAtomic.intValue();
        int attendancePercentage = 0;
        if (completedEvents != 0) {
            attendancePercentage = 100 / (totalEvents - leftEvents) * completedEvents;
        }

        return AttendanceDTO
                .builder()
                .totalEvents(totalEvents)
                .leftEvents(leftEvents)
                .completedEvents(completedEvents)
                .attendancePercentage(attendancePercentage).build();
    }
}