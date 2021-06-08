package com.tracking.attendance.qr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupDTO {
    private int id;
    private String name;
    @ApiModelProperty(hidden = true)
    private List<UserSmallDTO> assignedStudents;
}
