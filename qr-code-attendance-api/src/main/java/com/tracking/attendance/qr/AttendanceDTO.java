package com.tracking.attendance.qr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDTO {
    private int totalEvents;
    private int completedEvents;
    private int leftEvents;
    private int attendancePercentage;
}
