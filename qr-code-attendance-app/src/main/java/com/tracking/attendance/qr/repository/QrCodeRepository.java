package com.tracking.attendance.qr.repository;

import com.tracking.attendance.qr.model.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Integer> {
    Optional<QrCode> findByQrId(String qrId);

    Optional<QrCode> findByEventId(int eventId);
}