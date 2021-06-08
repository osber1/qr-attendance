package com.tracking.attendance.qr.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ImportUserRecord;
import com.tracking.attendance.qr.AttendanceDTO;
import com.tracking.attendance.qr.exception.NotFoundException;
import com.tracking.attendance.qr.model.Group;
import com.tracking.attendance.qr.model.User;
import com.tracking.attendance.qr.repository.UserRepository;
import com.tracking.attendance.qr.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private static final String USER_NOT_FOUND = "User with id %s not found.";
    private final UserRepository repository;
    private final GroupService groupService;

    public User create(User user, Jwt jwt) {
        int size = getAll().size();
        if (size != 0) {
            user.setRole("ROLE_STUDENT");
        } else {
            user.setRole("ROLE_ADMIN");
        }
        user.setEmail(jwt.getClaimAsString("email"));
        user.setId(jwt.getClaimAsString("user_id"));
        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public Collection<User> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public User getOne(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    public User update(User user) {
        if (!repository.existsById(user.getId())) {
            throw new NotFoundException(USER_NOT_FOUND + user.getId());
        }
        return repository.save(user);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(USER_NOT_FOUND + id);
        }
        repository.deleteById(id);
    }

    public AttendanceDTO getUserAttendance(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));

        AtomicInteger allEventsAtomic = new AtomicInteger();
        AtomicInteger leftEventsAtomic = new AtomicInteger();
        AtomicInteger completedEventsAtomic = new AtomicInteger();

        user.getAttendableLectures()
                .forEach(l -> allEventsAtomic.addAndGet(l.getEvents().size()));

        user.getAttendableLectures().parallelStream()
                .forEach(l -> leftEventsAtomic.addAndGet((int) l.getEvents().parallelStream()
                        .filter(e -> e.getStartDate().isAfter(Util.getVilniusTime()))
                        .count()));

        user.getAttendableLectures().parallelStream()
                .forEach(l -> l.getEvents().forEach(e -> completedEventsAtomic.addAndGet((int) e.getAttendedStudents()
                        .parallelStream()
                        .filter(s -> s.getStudentId().equals(userId) && s.isCompleted())
                        .count())));

        int totalEvents = allEventsAtomic.intValue();
        int leftEvents = leftEventsAtomic.intValue();
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

    @SneakyThrows
    public List<String> addUsersWithoutPasswords(MultipartFile file) {
        String line;
        InputStream is = file.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<ImportUserRecord> users = new ArrayList<>();
        List<String> emails = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] params = line.split(";");
            String displayName = params[0] + " " + params[1];
            String email = params[2];
            String id = UUID.randomUUID().toString();

            ImportUserRecord firebaseUser = createFirebaseUser(displayName, email, id);
            createUserToRepo(params, id);

            users.add(firebaseUser);
            emails.add(email);
        }
        FirebaseAuth.getInstance().importUsers(users);
        return emails;
    }

    private ImportUserRecord createFirebaseUser(String displayName, String email, String id) {
        return ImportUserRecord.builder()
                .setUid(id)
                .setDisplayName(displayName)
                .setEmail(email)
                .setEmailVerified(true)
                .build();
    }

    private void createUserToRepo(String[] params, String id) {
        User repoUser = new User();
        repoUser.setId(id);
        repoUser.setName(params[0]);
        repoUser.setSurname(params[1]);
        repoUser.setEmail(params[2]);
        repoUser.setRole("ROLE_STUDENT");
        repository.save(repoUser);
        if (params[3] != null) {
            setUserGroup(params, repoUser);
        }
    }

    private void setUserGroup(String[] params, User repoUser) {
        Optional<Group> optionalGroup = groupService.findGroupByName(params[3]);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            ArrayList<User> assignedStudents = new ArrayList<>(group.getAssignedStudents());
            assignedStudents.add(repoUser);
            group.setAssignedStudents(assignedStudents);
            groupService.update(group);
        } else {
            Group group = new Group();
            group.setName(params[3]);
            group.setAssignedStudents(List.of(repoUser));
            groupService.create(group);
        }
    }
}