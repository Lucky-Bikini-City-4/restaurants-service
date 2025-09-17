package com.dayaeyak.restaurants.seatSlots.dtos;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SeatSlotDto {

    private Long id;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int availableSeats;
}
