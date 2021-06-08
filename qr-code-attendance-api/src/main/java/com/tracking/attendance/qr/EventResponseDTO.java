package com.tracking.attendance.qr;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
public class EventResponseDTO {
    private int id;

    private IdNameDTO lecture;

    private Set<UserSmallDTO> attendableStudents;

    private UserSmallDTO lector;

    private String title;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    private boolean isShareEnabled;

    private boolean isCheckOutRequired;

    private boolean isCheckedIn;

    private boolean isCheckedOut;

    private String eventsGroupId;

    private int actionsLimit;
}
