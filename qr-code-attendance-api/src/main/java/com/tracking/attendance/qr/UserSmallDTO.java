package com.tracking.attendance.qr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSmallDTO {
    @ApiModelProperty(hidden = true)
    private String id;
    private String name;
    private String surname;
    private String role;
    private IdNameDTO group;
}
