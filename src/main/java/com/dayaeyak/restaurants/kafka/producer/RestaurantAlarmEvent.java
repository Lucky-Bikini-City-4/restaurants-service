package com.dayaeyak.restaurants.kafka.producer;

import com.dayaeyak.restaurants.kafka.producer.dtos.ServiceRegisterRequestDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RestaurantAlarmEvent extends ApplicationEvent {
    private final String topic;
    private final ServiceRegisterRequestDto dto;

    public RestaurantAlarmEvent(Object source, String topic, ServiceRegisterRequestDto dto) {
        super(source);
        this.topic = topic;
        this.dto = dto;
    }
}
