package com.tracking.attendance.qr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @ApiModelProperty(hidden = true)
    private String id;
    private String name;
    private String surname;
    @ApiModelProperty(hidden = true)
    private String email;
    @ApiModelProperty(hidden = true)
    private IdNameDTO group;
    @ApiModelProperty(hidden = true)
    private IdNameDTO faculty;
    @ApiModelProperty(hidden = true)
    private String role;
    @ApiModelProperty(hidden = true)
    private AttendanceDTO attendance;
}
