package com.dayaeyak.restaurants.restaurants.enums;

import java.time.DayOfWeek;
import java.util.Optional;

public enum ClosedDays {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    NONE; // 매일 영업

    public static ClosedDays fromDayOfWeek(DayOfWeek dow) {
        return switch(dow){
            case MONDAY -> MONDAY;
            case TUESDAY -> TUESDAY;
            case WEDNESDAY -> WEDNESDAY;
            case THURSDAY -> THURSDAY;
            case FRIDAY -> FRIDAY;
            case SATURDAY -> SATURDAY;
            case SUNDAY -> SUNDAY;
        };
    }
}
