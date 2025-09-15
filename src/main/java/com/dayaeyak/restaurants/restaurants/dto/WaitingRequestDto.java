package com.dayaeyak.restaurants.restaurants.dto;

import com.dayaeyak.restaurants.restaurants.enums.WaitingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaitingRequestDto {
    private WaitingStatus status;
}
