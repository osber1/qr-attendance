package com.tracking.attendance.qr.controller;


import com.tracking.attendance.qr.UserDTO;
import com.tracking.attendance.qr.mapper.Mapper;
import com.tracking.attendance.qr.model.User;
import com.tracking.attendance.qr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final Mapper mapper;

    @PostMapping
    public UserDTO create(@RequestBody @Valid UserDTO userDTO, @AuthenticationPrincipal Jwt jwt) throws ParseException {
        User user = mapper.userToEntity(userDTO);
        return mapper.userToDTO(service.create(user, jwt));
    }

    @GetMapping
    public Collection<UserDTO> getAll() {
        return mapper.userToDTOs(service.getAll());
    }

    @GetMapping("{id}")
    public UserDTO getOne(@PathVariable String id) {
        UserDTO userDTO = mapper.userToDTO(service.getOne(id));
        userDTO.setAttendance(service.getUserAttendance(id));
        return userDTO;
    }

    @PutMapping
    public UserDTO update(@RequestBody @Valid UserDTO userDTO) {
        User user = mapper.userToEntity(userDTO);
        return mapper.userToDTO(service.update(user));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("importStudents")
    public List<String> addStudentsFromCSV(@RequestParam MultipartFile file) {
        return service.addUsersWithoutPasswords(file);
    }
}