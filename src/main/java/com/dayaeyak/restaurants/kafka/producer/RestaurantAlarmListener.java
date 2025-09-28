package com.dayaeyak.restaurants.kafka.producer;

import com.dayaeyak.restaurants.kafka.producer.enums.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RestaurantAlarmListener {

    private final ProducerService producerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)  //트랜잭션 commit 이후에만 리스너가 호출됨
    public void handleRestaurantAlarm(RestaurantAlarmEvent event) {
        producerService.sendRegisterResult(event.getTopic(), null, event.getDto());
    }
}
