package com.dayaeyak.restaurants.seats.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SeatAvailabilityDto {
    private LocalDate date;
    private int availableSeats;
}
