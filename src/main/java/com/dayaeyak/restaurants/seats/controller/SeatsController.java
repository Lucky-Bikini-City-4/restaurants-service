package com.dayaeyak.restaurants.seats.controller;

import com.dayaeyak.restaurants.common.responses.ApiResponse;
import com.dayaeyak.restaurants.seats.dto.SeatAvailabilityDto;
import com.dayaeyak.restaurants.seats.dto.SeatsRequestDto;
import com.dayaeyak.restaurants.seats.service.SeatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurantSeats")
public class SeatsController {

    private final SeatsService seatsService;

    // 잔여좌석 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<ApiResponse<SeatAvailabilityDto>> getSeats(
            @PathVariable Long restaurantId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    )                          // @DateTimeFormat: 문자열 파라미터를 날짜/시간 객체로 변환, iso = DateTimeFormat.ISO.DATE_TIME: 표준날짜 시간 포맷
    {
        SeatAvailabilityDto dto = seatsService.getSeats(restaurantId, date);
        return ApiResponse.success(HttpStatus.OK, "조회 성공", dto);
    }

    // 좌석 예약(차감)
    @GetMapping("/reserve")
    public ResponseEntity<ApiResponse<SeatAvailabilityDto>> reserveSeats(
            @RequestBody SeatsRequestDto request
    ) {
        SeatAvailabilityDto dto = seatsService.reserveSeats(
                request.getRestaurantId(),
                request.getDate(),
                request.getCount()
        );
        return ApiResponse.success(HttpStatus.OK, "예약 성공", dto);
    }
}
