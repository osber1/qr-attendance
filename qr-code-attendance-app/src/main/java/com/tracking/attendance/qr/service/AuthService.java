package com.tracking.attendance.qr.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.tracking.attendance.qr.exception.UserRetrievalFailedException;
import com.tracking.attendance.qr.exception.UserUpdateFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final FirebaseAuth firebaseAuth;

    public UserRecord getUser(String uid) {
        try {
            return firebaseAuth.getUser(uid);
        } catch (FirebaseAuthException ex) {
            throw new UserRetrievalFailedException("Error occurred while trying to get user with id " + uid);
        }
    }

    public UserRecord setStudent(String uid) {
        return addRole(uid, "ROLE_STUDENT");
    }

    public UserRecord setLecturer(String uid) {
        return addRole(uid, "ROLE_LECTURER");
    }

    public UserRecord setAdmin(String uid) {
        return addRole(uid, "ROLE_ADMIN");
    }

    private UserRecord addRole(String uid, String role) {
        Map<String, Object> additionalClaims = Collections.singletonMap("ROLE", role);
        UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(uid);
        updateRequest.setCustomClaims(additionalClaims);
        try {
            return firebaseAuth.updateUser(updateRequest);
        } catch (FirebaseAuthException e) {
            throw new UserUpdateFailedException("User not found with id:" + uid);
        }
    }
}
