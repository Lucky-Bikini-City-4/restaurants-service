package com.dayaeyak.restaurants.restaurants.dto;

import com.dayaeyak.restaurants.restaurants.enums.ActivationStatus;
import com.dayaeyak.restaurants.restaurants.enums.ClosedDays;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.dayaeyak.restaurants.restaurants.enums.WaitingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class RestaurantResponseDto {
    private Long id;
    private String name;
    private Long sellerId;
    private String address;
    private String phoneNumber;
    private ClosedDays closedDay;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    private RestaurantType type;
    private int capacity;
    private ActivationStatus isActivation;
    private String city;
    private WaitingStatus waitingActivation;
}