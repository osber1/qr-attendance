package com.tracking.attendance.qr.repository;

import com.tracking.attendance.qr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsById(String id);

    void deleteById(String id);

    Optional<User> findById(String id);
}
