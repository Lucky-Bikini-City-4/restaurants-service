package com.dayaeyak.restaurants.restaurants.dto;

import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.enums.WaitingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaitingStatusDto {
    private Long id;
    private WaitingStatus waitingActivation;

    public static WaitingStatusDto from(Restaurant restaurant) {
        WaitingStatusDto dto = new WaitingStatusDto();
        dto.setId(restaurant.getId());
        dto.setWaitingActivation(restaurant.getWaitingActivation());
        return dto;
    }
}
