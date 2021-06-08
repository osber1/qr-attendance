package com.tracking.attendance.qr;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class EventRequestDTO {
    private int id;

    //    @NotNull
    @ApiModelProperty(hidden = true)
    private IdNameDTO lecture;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    //    @NotNull
    @ApiModelProperty(hidden = true)
    private Set<UserSmallDTO> attendableStudents;

    //    @NotNull
    @ApiModelProperty(hidden = true)
    private UserSmallDTO lector;

    @NotNull
    private String title;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Periodicity periodicity;

    private boolean isShareEnabled;

    private boolean isCheckOutRequired;

    private int actionsLimit;
}
