package com.dayaeyak.restaurants.restaurants.dto;

import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.enums.ActivationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivationStatusDto {
    private Long id;
    private ActivationStatus isActivation;

    public static ActivationStatusDto from(Restaurant restaurant) {
        ActivationStatusDto dto = new ActivationStatusDto();
        dto.setId(restaurant.getId());
        dto.setIsActivation(restaurant.getIsActivation());
        return dto;
    }
}
