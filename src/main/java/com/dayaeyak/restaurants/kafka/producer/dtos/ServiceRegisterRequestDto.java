package com.dayaeyak.restaurants.kafka.producer.dtos;

import com.dayaeyak.restaurants.kafka.producer.enums.ServiceType;
import com.dayaeyak.restaurants.kafka.producer.enums.Status;

public record ServiceRegisterRequestDto(
        Long userId, // 점주 id
        ServiceType serviceType, // 서비스 타입은 음식점으로 고정
        Long serviceId, // 생성된 음식점 id
        String userName, // 음식점 이름
        Status status  //CREATED로 고정
) {
}
