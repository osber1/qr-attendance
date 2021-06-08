package com.tracking.attendance.qr.controller;

import com.google.firebase.auth.UserRecord;
import com.tracking.attendance.qr.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("{id}")
    public UserRecord getUser(@PathVariable String id) {
        return authService.getUser(id);
    }

    @PostMapping("/student/{id}")
    public UserRecord setStudent(@PathVariable String id) {
        return authService.setStudent(id);
    }

    @PostMapping("lecturer/{id}")
    public UserRecord setLecturer(@PathVariable String id) {
        return authService.setLecturer(id);
    }

    @PostMapping("admin/{id}")
    public UserRecord setAdmin(@PathVariable String id) {
        return authService.setAdmin(id);
    }
}
