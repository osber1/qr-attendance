package com.tracking.attendance.qr;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class UserWithTimestampDTO extends UserSmallDTO {
    private ZonedDateTime checkInDatestamp;
    private ZonedDateTime checkOutDatestamp;
}
