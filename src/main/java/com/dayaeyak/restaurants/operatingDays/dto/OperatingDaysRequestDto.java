package com.dayaeyak.restaurants.operatingDays.dto;


import com.dayaeyak.restaurants.restaurants.enums.ClosedDays;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OperatingDaysRequestDto {
    private LocalDate date;
    private boolean isOpen;
    private ClosedDays operatingDate;
    private Long restaurantId;
}
