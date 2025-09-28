package com.dayaeyak.restaurants.kafka.producer;

import com.dayaeyak.restaurants.kafka.producer.enums.BusinessType;
import com.dayaeyak.restaurants.kafka.producer.enums.ServiceType;

import static com.dayaeyak.restaurants.kafka.producer.enums.ServiceType.*;

public class ServiceTypeMapper {
    public static ServiceType fromBusinessType(BusinessType businessType) {
        return switch (businessType){
            case EXHIBITION -> EXHIBITION;
            case RESTAURANT ->  RESTAURANT;
            case PERFORMANCE ->  PERFORMANCE;
        };
    }
}

