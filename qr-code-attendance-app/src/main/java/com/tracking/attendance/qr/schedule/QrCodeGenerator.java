package com.tracking.attendance.qr.schedule;

import com.tracking.attendance.qr.model.QrCode;
import com.tracking.attendance.qr.repository.EventRepository;
import com.tracking.attendance.qr.repository.QrCodeRepository;
import com.tracking.attendance.qr.repository.StudentRepository;
import com.tracking.attendance.qr.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class QrCodeGenerator {
    private final EventRepository eventRepository;
    private final QrCodeRepository qrCodeRepository;
    private final StudentRepository studentRepository;

    @Scheduled(cron = "*/10 * * * * *")
    private void generateQrId() {
        eventRepository.findAll()
                .parallelStream()
                .filter(e -> e.getStartDate().isBefore(Util.getVilniusTime()))
                .filter(e -> e.getEndDate().isAfter(Util.getVilniusTime()))
                .forEach(s -> {
                    Optional<QrCode> qrCodeOptional = qrCodeRepository.findByEventId(s.getId());
                    ZonedDateTime now = Util.getVilniusTime();
                    String newUuid = UUID.randomUUID().toString();
                    ZonedDateTime newTime = now.plusSeconds(10);
                    if (qrCodeOptional.isPresent()) {
                        QrCode qrCode = qrCodeOptional.get();
                        if (qrCode.getValidTill().minusSeconds(1).isBefore(now)) {
                            qrCode.setValidTill(newTime);
                            qrCode.setQrId(newUuid);
                            qrCodeRepository.save(qrCode);
                        }
                    } else {
                        QrCode newQrCode = QrCode.builder().qrId(newUuid).validTill(newTime).event(s).build();
                        qrCodeRepository.save(newQrCode);
                    }
                });
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void cleanDatabase() {
        qrCodeRepository.findAll().forEach(s -> {
            if (s.getValidTill().isBefore(Util.getVilniusTime()) || s.getEvent() == null) {
                qrCodeRepository.deleteById(s.getId());
            }
        });

        studentRepository.findAll().forEach(s -> {
            if (s.getEvent() == null) {
                studentRepository.deleteById(s.getId());
            }
        });
    }
}
