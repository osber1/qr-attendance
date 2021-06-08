package com.tracking.attendance.qr.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Util {
    public static ZonedDateTime getVilniusTime() {
        return ZonedDateTime.now(ZoneId.of("Europe/Vilnius"));
    }
}
