package com.dayaeyak.restaurants.operatingDays.controller;


import com.dayaeyak.restaurants.common.responses.ApiResponse;
import com.dayaeyak.restaurants.operatingDays.dto.OperatingDaysResponseDto;
import com.dayaeyak.restaurants.operatingDays.service.OperatingDaysService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurantOpeningDates")
public class OperatingDaysController {

    private final OperatingDaysService service;

    // 운영일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<OperatingDaysResponseDto>>> getOperatingDays(@PathVariable("id") Long id) {
        List<OperatingDaysResponseDto> dtoList = service.getOperatingDays(id);
        return ApiResponse.success(HttpStatus.OK, "조회 성공", dtoList);
    }
}
