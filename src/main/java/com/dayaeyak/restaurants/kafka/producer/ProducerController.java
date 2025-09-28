package com.dayaeyak.restaurants.kafka.producer;

import com.dayaeyak.restaurants.kafka.producer.dtos.ServiceRegisterRequestDto;
import com.dayaeyak.restaurants.kafka.producer.enums.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    @PostMapping("/send-register/topic")
    public String sendRegisterMessage(@RequestParam("topic") String topic,
                                      @RequestParam("key") String key,
                                      @RequestBody ServiceRegisterRequestDto dto) {
        producerService.sendRegisterResult(topic, key, dto);
        return "ServiceRegisterRequestDto message sent to Kafka topic: " + topic;
    }

}

