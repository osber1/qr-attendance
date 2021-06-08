package com.tracking.attendance.qr.service;

import com.tracking.attendance.qr.Periodicity;
import com.tracking.attendance.qr.QrCodeDTO;
import com.tracking.attendance.qr.UserWithTimestampDTO;
import com.tracking.attendance.qr.exception.BadRequestException;
import com.tracking.attendance.qr.exception.NotFoundException;
import com.tracking.attendance.qr.model.*;
import com.tracking.attendance.qr.repository.*;
import com.tracking.attendance.qr.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    public static final String EUROPE_VILNIUS = "Europe/Vilnius";
    private static final String EVENT_NOT_FOUND = "There is no event with id: ";
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final QrCodeRepository qrCodeRepository;
    private final StudentRepository studentRepository;

    public Collection<Event> create(Event event, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Periodicity periodicity) {
        String uuid = UUID.randomUUID().toString();
        Collection<Event> events = generateEvents(event, startDate, endDate, startTime, endTime, periodicity, uuid);
        for (Event e : events) {
            if (e.getAttendableStudents() != null) {
                setAllStudentsFromDTOToNull(e);
                setStudents(e);
            }
            if (e.getLector() != null) {
                setLectorToNull(e);
                setLector(e);
            }
            if (e.getLecture() != null) {
                setLectureNull(e);
                setLecture(e);
            }
        }
        return events;
    }

    @Transactional(readOnly = true)
    public Collection<Event> getAll() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Event getOne(Integer id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND + id));
    }

    public Event update(Event event) {
        if (!eventRepository.existsById(event.getId())) {
            throw new NotFoundException(EVENT_NOT_FOUND + event.getId());
        } else {
            if (event.getAttendableStudents() != null) {
                setAllStudentsFromRepoToNull(event);
                setStudents(event);
            }
            if (event.getLector() != null) {
                setLectorToNull(event);
                setLector(event);
            }
            if (event.getLecture() != null) {
                setLectureNull(event);
                setLecture(event);
            }
        }
        return eventRepository.save(event);
    }

    public void delete(Integer id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(EVENT_NOT_FOUND + id);
        }
        eventRepository.deleteById(id);
    }

    public Collection<Event> editEventsByGroupId(Event event) {
        Collection<Event> returnList = new ArrayList<>();
        getEventsByGroupId(event.getEventsGroupId())
                .forEach(e -> {
                    Event updatedEvent = update(e);
                    returnList.add(updatedEvent);
                });
        return returnList;
    }

    public Collection<Event> getEventsByGroupId(String id) {
        return eventRepository.findByEventsGroupId(id);
    }

    public void deleteEventsByGroupId(String id) {
        if (!eventRepository.existsByEventsGroupId(id)) {
            throw new NotFoundException("There is no id: " + id);
        }
        eventRepository.deleteByEventsGroupId(id);
    }

    public Collection<Event> getStudentEventsByUserId(String userId) {
        User user = getUser(userId);
        Set<Event> studentEvents = user.getStudentEvents();
        studentEvents.forEach(e -> {
            e.setCheckedIn(false);
            e.setCheckedOut(false);
        });

        studentEvents.forEach(e -> e.getAttendedStudents().forEach(s -> {
            if (s.getStudentId().equals(user.getId()) && e.getId() == s.getEvent().getId()) {
                e.setCheckedIn(s.isCheckedIn());
                e.setCheckedOut(s.isCheckedOut());
            }
        }));
        return studentEvents;
    }

    public Collection<Event> getLectorEventsByUserId(String userId) {
        User user = getUser(userId);
        List<Event> lectorEvents = user.getLectorEvents();
        lectorEvents.forEach(e -> {
            e.setCheckedIn(false);
            e.setCheckedOut(false);
        });

        lectorEvents.forEach(e -> e.getAttendedStudents().forEach(s -> {
            if (s.getStudentId().equals(user.getId()) && e.getId() == s.getEvent().getId()) {
                e.setCheckedIn(s.isCheckedIn());
                e.setCheckedOut(s.isCheckedOut());
            }
        }));
        return lectorEvents;
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + userId));
    }

    public Collection<Event> getLectorEventsByUserIdAndLectureId(String userId, int lectureId) {
        User user = getUser(userId);
        List<Event> lectorEvents = user.getLectorEvents()
                .stream()
                .filter(event -> event.getLecture().getId() == lectureId)
                .collect(Collectors.toList());
        lectorEvents.forEach(e -> {
            e.setCheckedIn(false);
            e.setCheckedOut(false);
        });
        lectorEvents.forEach(e -> e.getAttendedStudents().forEach(s -> {
            if (s.getStudentId().equals(user.getId()) && e.getId() == s.getEvent().getId()) {
                e.setCheckedIn(s.isCheckedIn());
                e.setCheckedOut(s.isCheckedOut());
            }
        }));
        return lectorEvents;
    }

    public Collection<Event> getStudentEventsByUserIdAndLectureId(String userId, int lectureId) {
        User user = getUser(userId);
        List<Event> studentEvents = user.getStudentEvents()
                .stream()
                .filter(event -> event.getLecture().getId() == lectureId)
                .collect(Collectors.toList());

        studentEvents.forEach(e -> {
            e.setCheckedIn(false);
            e.setCheckedOut(false);
        });

        studentEvents.forEach(e -> e.getAttendedStudents().forEach(s -> {
            if (s.getStudentId().equals(user.getId()) && e.getId() == s.getEvent().getId()) {
                e.setCheckedIn(s.isCheckedIn());
                e.setCheckedOut(s.isCheckedOut());
            }
        }));
        return studentEvents;
    }

    public Collection<Event> getLectorEventsByDateInterval(String userId, ZonedDateTime dateFrom, ZonedDateTime dateTo) {
        User user = getUser(userId);
        List<Event> lectorEvents = user.getLectorEvents()
                .stream()
                .filter(u -> u.getStartDate().isAfter(dateFrom) && u.getStartDate().isBefore(dateTo))
                .collect(Collectors.toList());
        lectorEvents.forEach(e -> {
            e.setCheckedIn(false);
            e.setCheckedOut(false);
        });

        lectorEvents.forEach(e -> e.getAttendedStudents().forEach(s -> {
            if (s.getStudentId().equals(user.getId()) && e.getId() == s.getEvent().getId()) {
                e.setCheckedIn(s.isCheckedIn());
                e.setCheckedOut(s.isCheckedOut());
            }
        }));
        return lectorEvents;
    }

    public Collection<Event> getStudentEventsByDateInterval(String userId, ZonedDateTime dateFrom, ZonedDateTime dateTo) {
        User user = getUser(userId);
        List<Event> studentEvents = user.getStudentEvents()
                .stream()
                .filter(u -> u.getStartDate().isAfter(dateFrom) && u.getStartDate().isBefore(dateTo))
                .collect(Collectors.toList());
        studentEvents.forEach(e -> {
            e.setCheckedIn(false);
            e.setCheckedOut(false);
        });

        studentEvents.forEach(e -> e.getAttendedStudents().forEach(s -> {
            if (s.getStudentId().equals(user.getId()) && e.getId() == s.getEvent().getId()) {
                e.setCheckedIn(s.isCheckedIn());
                e.setCheckedOut(s.isCheckedOut());
            }
        }));
        return studentEvents;
    }

    public void checkInToEvent(String userId, String qrId, int eventId) {
        checkIfUserExists(userId);
        QrCode qrCode = getQrCode(qrId);
        ZonedDateTime now = Util.getVilniusTime();
        ZonedDateTime validTill = qrCode.getValidTill();
        if (now.isBefore(validTill) || now.isEqual(validTill)) {
            checkInStudent(userId, eventId, now);
            generateNewQrCode(qrId);
        } else {
            throw new BadRequestException("Bad QR code.");
        }
    }

    public void checkOutFromEvent(String userId, String qrId, int eventId) {
        checkIfUserExists(userId);
        QrCode qrCode = getQrCode(qrId);
        ZonedDateTime now = Util.getVilniusTime();
        ZonedDateTime validTill = qrCode.getValidTill();
        if (now.isBefore(validTill) || now.isEqual(validTill)) {
            checkOutStudent(userId, eventId, now);
            generateNewQrCode(qrId);
        } else {
            throw new BadRequestException("Bad QR code.");
        }
    }

    private QrCode getQrCode(String qrId) {
        return qrCodeRepository.findByQrId(qrId)
                .orElseThrow(() -> new BadRequestException("Qr code does not exist."));
    }

    public void checkInStudentToEvent(String userId, int eventId) {
        checkIfUserExists(userId);
        checkInStudent(userId, eventId, Util.getVilniusTime());
    }

    public void checkOutStudentFromEvent(String userId, int eventId) {
        checkIfUserExists(userId);
        checkOutStudent(userId, eventId, Util.getVilniusTime());
    }

    private void setLecture(Event event) {
        Lecture lecture = lectureRepository.findById(event.getLecture().getId())
                .orElseThrow(() -> new NotFoundException("Lecture not found: " + event.getLecture().getId()));
        List<Event> events = lecture.getEvents();
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event);
        lectureRepository.save(lecture);
    }

    private void setLector(Event event) {
        User lector = getUser(event.getLector().getId());
        List<Event> lectorEvents = lector.getLectorEvents();
        if (lectorEvents == null) {
            lectorEvents = new ArrayList<>();
        }
        lectorEvents.add(event);
        userRepository.save(lector);
    }

    private void setStudents(Event event) {
        List<User> students = event.getAttendableStudents()
                .stream()
                .map(u -> userRepository.findById(u.getId()).orElseThrow(() -> new NotFoundException("User not found: " + u.getId())))
                .collect(Collectors.toList());
        students.forEach(s -> userRepository.findById(s.getId()).get().getStudentEvents().add(event));
        userRepository.saveAll(students);
    }

    private void setAllStudentsFromRepoToNull(Event event) {
        List<User> students = userRepository.findAll()
                .stream()
                .peek(u -> {
                    if (u.getStudentEvents() != null) {
                        Set<Event> studentEvents = u.getStudentEvents();
                        studentEvents.removeIf(e -> e.getId() == event.getId());
                        u.setStudentEvents(studentEvents);
                        userRepository.save(u);
                    }
                }).collect(Collectors.toList());
        userRepository.saveAll(students);
    }

    private void setLectureNull(Event event) {
        Lecture lecture = lectureRepository.findById(event.getLecture().getId())
                .orElseThrow(() -> new NotFoundException("Lecture not found: " + event.getLecture().getId()));
        List<Event> events = lecture.getEvents();
        events.removeIf(e -> e.getId() == event.getId());
        lectureRepository.save(lecture);
    }

    private void setLectorToNull(Event event) {
        User lector = getUser(event.getLector().getId());
        List<Event> events = lector.getLectorEvents();
        events.removeIf(e -> e.getId() == event.getId());
        userRepository.save(lector);
    }

    private void setAllStudentsFromDTOToNull(Event event) {
        Set<User> students = event.getAttendableStudents()
                .stream()
                .map(u -> userRepository.findById(u.getId()).orElseThrow(() -> new NotFoundException("User not found: " + u.getId())))
                .peek(u -> {
                    if (u.getStudentEvents() != null) {
                        Set<Event> studentEvents = u.getStudentEvents();
                        studentEvents.removeIf(e -> e.getId() == event.getId());
                        u.setStudentEvents(studentEvents);
                    }
                }).collect(Collectors.toSet());
        userRepository.saveAll(students);
    }

    private void checkInStudent(String userId, int eventId, ZonedDateTime now) {
        Event event = getOne(eventId);
        checkIfStudentBelongToEvent(userId, event);
        Student student = generateStudent(userId, now, event);
        if (event.isShareEnabled()) {
            student.setActionsLeft(event.getActionsLimit());
        }
        if (!event.isCheckOutRequired()) {
            student.setCompleted(true);
        }
        if (event.getAttendedStudents() == null) {
            generateNewStudentsList(event, student);
        } else {
            addStudentToListIfNotExist(userId, event, student);
        }
        studentRepository.save(student);
        eventRepository.save(event);
    }

    private void checkOutStudent(String userId, int eventId, ZonedDateTime now) {
        Event event = getOne(eventId);
        checkIfStudentBelongToEvent(userId, event);
        Student studentToUpdate = getCheckedInStudent(userId, event);
        checkOutStudent(now, studentToUpdate, eventId);
    }

    public void checkInOtherStudentToEvent(String userId, int eventId, String senderId) {
        checkActionsLeft(senderId, eventId);
        checkInStudent(userId, eventId, ZonedDateTime.now());
    }

    public void checkOutOtherStudentFromEvent(String userId, int eventId, String senderId) {
        checkActionsLeft(senderId, eventId);
        checkOutStudent(userId, eventId, ZonedDateTime.now());
    }

    private void checkActionsLeft(String senderId, int eventId) {
        Student student = studentRepository.findByStudentIdAndEventId(senderId, eventId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + senderId));
        if (student.getActionsLeft() > 0) {
            student.setActionsLeft(student.getActionsLeft() - 1);
            studentRepository.save(student);
        } else {
            throw new BadRequestException("Actions limit exceeded.");
        }
    }

    private Student getCheckedInStudent(String userId, Event event) {
        return event.getAttendedStudents()
                .parallelStream()
                .filter(s -> s.getStudentId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Student is not checked in."));
    }

    private void addStudentToListIfNotExist(String userId, Event event, Student student) {
        List<Student> students = event.getAttendedStudents().stream()
                .filter(s -> s.getStudentId().equals(userId))
                .collect(Collectors.toList());
        if (!students.isEmpty()) {
            throw new BadRequestException("Already checked in.");
        } else {
            addStudentToList(event, student);
        }
    }

    private void checkIfStudentBelongToEvent(String userId, Event event) {
        event.getAttendableStudents()
                .stream()
                .filter(s -> s.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Student can't be checked in."));
    }

    private void addStudentToList(Event event, Student student) {
        Set<Student> attendedStudents = event.getAttendedStudents();
        attendedStudents.add(student);
        event.setAttendedStudents(attendedStudents);
    }

    private void generateNewStudentsList(Event event, Student student) {
        Set<Student> students = new HashSet<>();
        students.add(student);
        event.setAttendedStudents(students);
    }

    private Student generateStudent(String userId, ZonedDateTime now, Event event) {
        return Student.builder()
                .studentId(userId)
                .checkedInTime(now)
                .isCheckedIn(true)
                .event(event)
                .build();
    }

    private void checkOutStudent(ZonedDateTime now, Student studentToUpdate, int eventId) {
        if (studentToUpdate.isCheckedOut()) {
            throw new BadRequestException("Student already checked out.");
        }
        studentToUpdate.setCheckedOut(true);
        studentToUpdate.setCheckedOutTime(now);
        if (!studentToUpdate.isCompleted()) {
            studentToUpdate.setCompleted(true);
        }
        studentToUpdate.setActionsLeft(getOne(eventId).getActionsLimit());
        studentRepository.save(studentToUpdate);
    }

    private void checkIfUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User does not exist.");
        }
    }

    public QrCodeDTO getQrCode(int eventId) {
        QrCode qrCode = qrCodeRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Error while getting qr code id."));
        QrCodeDTO returnItem = new QrCodeDTO();
        returnItem.setQrId(qrCode.getQrId());
        return returnItem;
    }

    private void generateNewQrCode(String qrId) {
        QrCode qrCode = qrCodeRepository.findByQrId(qrId)
                .orElseThrow(() -> new NotFoundException("Error while getting qr code id."));
        String uuid = UUID.randomUUID().toString();
        ZonedDateTime time = Util.getVilniusTime().plusSeconds(10);
        qrCode.setValidTill(time);
        qrCode.setQrId(uuid);
        qrCodeRepository.save(qrCode);
    }

    public Collection<Event> generateEvents(Event event, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Periodicity periodicity, String uuid) {
        List<Event> events = new ArrayList<>();
        switch (periodicity) {
            case NONE:
                event.setStartDate(ZonedDateTime.of(startDate, startTime, ZoneId.of(EUROPE_VILNIUS)));
                event.setEndDate(ZonedDateTime.of(startDate, endTime, ZoneId.of(EUROPE_VILNIUS)));
                event.setEventsGroupId(uuid);
                eventRepository.save(event);
                events.add(event);
                break;
            case DAY:
                while (startDate.isBefore(endDate.plusDays(1))) {
                    if (!startDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !startDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                        saveEvents(event, events, startDate, endDate, startTime, endTime, uuid);
                    }
                    startDate = startDate.plusDays(1);
                }
                break;
            case WEEK:
                while (startDate.isBefore(endDate.plusDays(1))) {
                    saveEvents(event, events, startDate, endDate, startTime, endTime, uuid);
                    startDate = startDate.plusWeeks(1);
                }
                break;
            case WEEK2:
                while (startDate.isBefore(endDate.plusDays(1))) {
                    saveEvents(event, events, startDate, endDate, startTime, endTime, uuid);
                    startDate = startDate.plusWeeks(2);
                }
                break;
            case MONTH:
                while (startDate.isBefore(endDate.plusDays(1))) {
                    saveEvents(event, events, startDate, endDate, startTime, endTime, uuid);
                    startDate = startDate.plusMonths(1);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + periodicity);
        }
        return events;
    }

    private void saveEvents(Event event, List<Event> events, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String uuid) {
        if (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            Event newEvent = new Event();
            newEvent.setStartDate(ZonedDateTime.of(startDate, startTime, ZoneId.of(EUROPE_VILNIUS)));
            newEvent.setEndDate(ZonedDateTime.of(startDate, endTime, ZoneId.of(EUROPE_VILNIUS)));
            newEvent.setTitle(event.getTitle());
            newEvent.setLector(event.getLector());
            newEvent.setLecture(event.getLecture());
            newEvent.setAttendableStudents(event.getAttendableStudents());
            newEvent.setEventsGroupId(uuid);
            newEvent.setCheckOutRequired(event.isCheckOutRequired());
            newEvent.setShareEnabled(event.isShareEnabled());
            eventRepository.save(newEvent);
            events.add(newEvent);
        }
    }

    public Collection<UserWithTimestampDTO> getAttendedStudentsByEventId(int eventId) {
        Event event = getOne(eventId);
        return event.getAttendedStudents().stream()
                .map(s -> {
                    User user = getUser(s.getStudentId());
                    UserWithTimestampDTO returnUser = new UserWithTimestampDTO();
                    returnUser.setId(user.getId());
                    returnUser.setName(user.getName());
                    returnUser.setSurname(user.getSurname());
                    returnUser.setCheckInDatestamp(s.getCheckedInTime());
                    returnUser.setCheckOutDatestamp(s.getCheckedOutTime());
                    return returnUser;
                }).collect(Collectors.toList());
    }
}
