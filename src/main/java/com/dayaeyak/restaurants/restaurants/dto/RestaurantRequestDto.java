package com.dayaeyak.restaurants.restaurants.dto;

import com.dayaeyak.restaurants.restaurants.enums.ActivationStatus;
import com.dayaeyak.restaurants.restaurants.enums.ClosedDays;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.dayaeyak.restaurants.restaurants.enums.WaitingStatus;
import lombok.Data;

import java.time.LocalTime;

@Data
public class RestaurantRequestDto {
    private String name;
    private String address;
    private String phoneNumber;
    private ClosedDays closedDay;
    private LocalTime openTime;
    private LocalTime closeTime;
    private RestaurantType type;
    private int capacity;
    private ActivationStatus isActivation;
    private String city;
    private WaitingStatus waitingActivation;
}
