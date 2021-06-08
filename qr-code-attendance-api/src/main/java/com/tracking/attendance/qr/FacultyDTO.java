package com.tracking.attendance.qr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacultyDTO {
    private int id;
    private String name;
    @ApiModelProperty(hidden = true)
    private List<UserSmallDTO> users;
    @ApiModelProperty(hidden = true)
    private List<IdNameDTO> lectures;
}
