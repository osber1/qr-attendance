package com.tracking.attendance.qr.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.tracking.attendance.qr.exception.MissingPhoneException;
import com.tracking.attendance.qr.exception.UserRetrievalFailedException;
import com.tracking.attendance.qr.exception.UserUpdateFailedException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class FirebaseAuthService {

    private final FirebaseAuth firebaseAuth;

    public String getPhone(String uid) {
        var user = getUser(uid);
        var phoneNumber = user.getPhoneNumber();
        if (StringUtils.isBlank(phoneNumber)) {
            throw new MissingPhoneException();
        }
        UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(uid);
        updateRequest.setCustomClaims(new HashMap<>());
        updateUser(updateRequest);
        return phoneNumber;
    }

    private UserRecord getUser(String uid) {
        try {
            return firebaseAuth.getUser(uid);
        } catch (FirebaseAuthException ex) {
            throw new UserRetrievalFailedException(ex.getMessage());
        }
    }

    private void updateUser(UserRecord.UpdateRequest request) {
        try {
            firebaseAuth.updateUser(request);
        } catch (FirebaseAuthException ex) {
            throw new UserUpdateFailedException(ex.getMessage());
        }
    }
}
