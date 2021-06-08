package com.tracking.attendance.qr.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance(getFirebaseApp());
    }

    private FirebaseApp getFirebaseApp() {
        if (CollectionUtils.isNotEmpty(FirebaseApp.getApps())) {
            return FirebaseApp.getInstance();
        }
        return initializeFirebaseApp();
    }

    private FirebaseApp initializeFirebaseApp() {
        try {
            ClassPathResource resource = new ClassPathResource("phyiscalattendanceqr-firebase-adminsdk-lbdbk-5dd1a5a450.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
            return FirebaseApp.initializeApp(options);
        } catch (Exception ex) {
            throw new BeanCreationException("Failed to create FirebaseAuth bean", ex);
        }
    }
}
