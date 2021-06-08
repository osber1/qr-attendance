package com.tracking.attendance.qr.repository;

import com.tracking.attendance.qr.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByStudentIdAndEventId(String id, int eventId);
}