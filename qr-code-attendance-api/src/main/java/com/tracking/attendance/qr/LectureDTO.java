package com.tracking.attendance.qr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class LectureDTO {
    private int id;

    private String name;

    @ApiModelProperty(hidden = true)
    private Set<UserSmallDTO> assignedStudents;

    @ApiModelProperty(hidden = true)
    private Set<UserSmallDTO> assignedLectors;

    @ApiModelProperty(hidden = true)
    private List<EventResponseDTO> events;

    @ApiModelProperty(hidden = true)
    private IdNameDTO faculty;

    @ApiModelProperty(hidden = true)
    private AttendanceDTO attendance;
}