package com.dayaeyak.restaurants.operatingDays.dto;

import com.dayaeyak.restaurants.restaurants.enums.ClosedDays;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OperatingDaysResponseDto {

    private LocalDate date; // 오늘 날짜
    private ClosedDays operatingDate; // 현재 요일
    private boolean isOpen; // 해당 요일 운영 여부
    private List<SeatSlotDto> slots;

    @Getter
    @Setter
    public static class SeatSlotDto {
        private Long id;
        private LocalDate date;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private int availableSeats;
    }
}
