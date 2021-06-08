package com.tracking.attendance.qr.service;

import com.tracking.attendance.qr.exception.NotFoundException;
import com.tracking.attendance.qr.model.Faculty;
import com.tracking.attendance.qr.model.Lecture;
import com.tracking.attendance.qr.model.User;
import com.tracking.attendance.qr.repository.FacultyRepository;
import com.tracking.attendance.qr.repository.LectureRepository;
import com.tracking.attendance.qr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FacultyService {
    private static final String FACULTY_NOT_FOUND = "Faculty not found with id: ";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private final FacultyRepository repository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;

    public Faculty create(Faculty faculty) {
        repository.save(faculty);
        if (faculty.getUsers() != null) {
            setAllUsersFromDTOToNull(faculty);
            setUsersFaculty(faculty);
        }
        if (faculty.getLectures() != null) {
            setAllLecturesFromDTOToNull(faculty);
            setLectureFaculty(faculty);
        }
        return repository.findById(faculty.getId())
                .orElseThrow(() -> new NotFoundException(FACULTY_NOT_FOUND + faculty.getId()));
    }

    @Transactional(readOnly = true)
    public Collection<Faculty> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Faculty getOne(Integer id) {
        Optional<Faculty> faculty = repository.findById(id);
        if (faculty.isEmpty()) {
            throw new NotFoundException(FACULTY_NOT_FOUND + id);
        }
        return faculty.get();
    }

    public Faculty update(Faculty faculty) {
        if (!repository.existsById(faculty.getId())) {
            throw new NotFoundException(FACULTY_NOT_FOUND + faculty.getId());
        } else {
            if (faculty.getUsers() != null) {
                setAllUsersFromRepoToNull(faculty);
                setUsersFaculty(faculty);
            }
            if (faculty.getLectures() != null) {
                setAllLecturesFromRepoToNull(faculty);
                setLectureFaculty(faculty);
            }
            return repository.save(faculty);
        }
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(FACULTY_NOT_FOUND + id);
        }
        repository.deleteById(id);
    }

    private void setUsersFaculty(Faculty faculty) {
        List<User> assignedStudents = faculty.getUsers();
        assignedStudents.forEach(s -> {
            User user = userRepository.findById(s.getId()).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + s.getId()));
            user.setFaculty(faculty);
            userRepository.save(user);
        });
    }

    private void setLectureFaculty(Faculty faculty) {
        List<Lecture> lectures = faculty.getLectures();
        lectures.forEach(s -> {
            Lecture lecture = lectureRepository.findById(s.getId()).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + s.getId()));
            lecture.setFaculty(faculty);
            lectureRepository.save(lecture);
        });
    }

    private void setAllUsersFromRepoToNull(Faculty faculty) {
        List<User> allUsers = userRepository.findAll()
                .stream()
                .filter(u -> {
                    if (u.getFaculty() != null) {
                        return u.getFaculty().getId() == faculty.getId();
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
        allUsers.forEach(u -> u.setFaculty(null));
        userRepository.saveAll(allUsers);
    }

    private void setAllUsersFromDTOToNull(Faculty faculty) {
        List<User> allUsers = faculty.getUsers()
                .stream()
                .map(u -> userRepository.findById(u.getId()).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + u.getId())))
                .filter(u -> {
                    if (u.getFaculty() != null) {
                        return u.getFaculty().getId() == faculty.getId();
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
        allUsers.forEach(u -> u.setFaculty(null));
        userRepository.saveAll(allUsers);
    }

    private void setAllLecturesFromRepoToNull(Faculty faculty) {
        List<Lecture> allLectures = lectureRepository.findAll()
                .stream()
                .filter(u -> {
                    if (u.getFaculty() != null) {
                        return u.getFaculty().getId() == faculty.getId();
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
        allLectures.forEach(u -> u.setFaculty(null));
        lectureRepository.saveAll(allLectures);
    }

    private void setAllLecturesFromDTOToNull(Faculty faculty) {
        List<Lecture> allLectures = faculty.getLectures()
                .stream()
                .map(u -> lectureRepository.findById(u.getId()).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + u.getId())))
                .filter(u -> {
                    if (u.getFaculty() != null) {
                        return u.getFaculty().getId() == faculty.getId();
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
        allLectures.forEach(u -> u.setFaculty(null));
        lectureRepository.saveAll(allLectures);
    }
}