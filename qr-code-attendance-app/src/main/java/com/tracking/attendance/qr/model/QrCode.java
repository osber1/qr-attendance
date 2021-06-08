package com.tracking.attendance.qr.model;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_qr_code")
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String qrId;

    private ZonedDateTime validTill;

    @OneToOne
    private Event event;

    @PreRemove
    private void preRemove() {
        event.setQrCode(null);
    }
}
