package com.tracking.attendance.qr.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_event")
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    @ManyToMany(mappedBy = "studentEvents")
    private Set<User> attendableStudents;

    @ManyToOne
    @JoinColumn(name = "lector_id")
    private User lector;

    private boolean isShareEnabled;

    private boolean isCheckOutRequired;

    private String eventsGroupId;

    @OneToMany(mappedBy = "event")
    private Set<Student> attendedStudents;

    @OneToOne(mappedBy = "event")
    private QrCode qrCode;

    private int actionsLimit;

    private boolean isCheckedIn;

    private boolean isCheckedOut;

    @PreRemove
    private void preRemove() {
        attendableStudents.forEach(user -> user.getLectorEvents().remove(this));
        attendableStudents.forEach(user -> user.getStudentEvents().remove(this));
        attendedStudents.forEach(user -> user.setEvent(null));
        if (lector != null) {
            lector.getLectorEvents().remove(this);
        }
        if (qrCode != null) {
            qrCode.setEvent(null);
        }
    }
}
