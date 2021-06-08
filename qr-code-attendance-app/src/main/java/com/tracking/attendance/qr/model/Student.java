package com.tracking.attendance.qr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String studentId;

    private boolean isCheckedIn;

    private ZonedDateTime checkedInTime;

    private boolean isCheckedOut;

    private ZonedDateTime checkedOutTime;

    private boolean isLate;

    private boolean isCompleted;

    private int actionsLeft;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}