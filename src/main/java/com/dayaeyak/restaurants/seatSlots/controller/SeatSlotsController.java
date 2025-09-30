package com.dayaeyak.restaurants.seatSlots.controller;

import com.dayaeyak.restaurants.common.responses.ApiResponse;
import com.dayaeyak.restaurants.seatSlots.dtos.SeatSlotDto;
import com.dayaeyak.restaurants.seatSlots.service.SeatSlotsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/seats")
public class SeatSlotsController {

    private final SeatSlotsService service;

    //특정 레스토랑의 특정 날짜 예약 가능 좌석 조회
    @GetMapping("/{restaurantId}/{operatingDayId}")
    public ResponseEntity<ApiResponse<List<SeatSlotDto>>> getSeats(
            @PathVariable Long restaurantId,
            @PathVariable Long operatingDayId
    ) {
        List<SeatSlotDto> slots = service.getSlots(restaurantId, operatingDayId);
        return ApiResponse.success(HttpStatus.OK, "좌석 조회 성공", slots);
    }

    // 좌석 예약

    @PostMapping("/{restaurantId}/{operatingDayId}/{slotId}/reserve")
    public ResponseEntity<ApiResponse<SeatSlotDto>> reserveSeat(
            @PathVariable Long restaurantId,
            @PathVariable Long operatingDayId,
            @PathVariable Long slotId,
            @RequestParam int count
    ) {
        SeatSlotDto dto = service.reserveSlot(restaurantId,operatingDayId, slotId, count);
        return ApiResponse.success(HttpStatus.OK, "예약 성공", dto);
    }

}
