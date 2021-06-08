package com.tracking.attendance.qr.service;

import com.tracking.attendance.qr.exception.NotFoundException;
import com.tracking.attendance.qr.model.Group;
import com.tracking.attendance.qr.model.User;
import com.tracking.attendance.qr.repository.GroupRepository;
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
public class GroupService {
    private static final String GROUP_NOT_FOUND = "Group not found with id: ";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private final GroupRepository repository;
    private final UserRepository userRepository;

    public Group create(Group group) {
        repository.save(group);
        if (group.getAssignedStudents() != null) {
            setAllUsersFromDTOToNull(group);
            setUsersGroup(group);
        }
        return repository.findById(group.getId()).orElseThrow(() -> new NotFoundException(GROUP_NOT_FOUND + group.getId()));
    }

    @Transactional(readOnly = true)
    public Collection<Group> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Group getOne(Integer id) {
        Optional<Group> group = repository.findById(id);
        if (group.isEmpty()) {
            throw new NotFoundException(GROUP_NOT_FOUND + id);
        }
        return group.get();
    }

    public Group update(Group group) {
        if (!repository.existsById(group.getId())) {
            throw new NotFoundException(GROUP_NOT_FOUND + group.getId());
        } else {
            if (group.getAssignedStudents() != null) {
                setAllUsersFromRepoToNull(group);
                setUsersGroup(group);
            }
            return repository.save(group);
        }
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(GROUP_NOT_FOUND + id);
        } else {
            this.repository.deleteById(id);
        }
    }

    private void setUsersGroup(Group group) {
        List<User> assignedStudents = group.getAssignedStudents();
        assignedStudents.forEach(s -> {
            User user = userRepository.findById(s.getId()).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + s.getId()));
            user.setGroup(group);
            userRepository.save(user);
        });
    }

    private void setAllUsersFromRepoToNull(Group group) {
        List<User> allUsers = userRepository.findAll()
                .stream()
                .filter(u -> {
                    if (u.getGroup() != null) {
                        return u.getGroup().getId() == group.getId();
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
        allUsers.forEach(u -> u.setGroup(null));
        userRepository.saveAll(allUsers);
    }

    private void setAllUsersFromDTOToNull(Group group) {
        List<User> allUsers = group.getAssignedStudents()
                .stream()
                .map(u -> userRepository.findById(u.getId())
                        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + u.getId())))
                .filter(u -> {
                    if (u.getGroup() != null) {
                        return u.getGroup().getId() == group.getId();
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
        allUsers.forEach(u -> u.setGroup(null));
        userRepository.saveAll(allUsers);
    }

    public Optional<Group> findGroupByName(String name) {
        return repository.findByName(name);
    }
}