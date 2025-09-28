package com.dayaeyak.restaurants.kafka.producer.enums;

import com.dayaeyak.restaurants.kafka.producer.dtos.ServiceRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, ServiceRegisterRequestDto> kafkaTemplateSRR;

    // 음식점 등록 요청
    public void sendRegisterResult(String topic, String key, ServiceRegisterRequestDto dto) {
        kafkaTemplateSRR.send(topic, key, dto);
    }
}

