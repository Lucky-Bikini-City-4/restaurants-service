package com.dayaeyak.restaurants.restaurants.dto;


import com.dayaeyak.restaurants.restaurants.enums.ActivationStatus;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActivationRequestDto {
    private ActivationStatus status;
}
