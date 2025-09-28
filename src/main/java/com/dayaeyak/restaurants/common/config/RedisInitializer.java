package com.dayaeyak.restaurants.common.config;

import com.dayaeyak.restaurants.seatSlots.entity.SeatSlots;
import com.dayaeyak.restaurants.seatSlots.repository.SeatSlotsRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisInitializer {

    private final SeatSlotsRepository repository;
    private final RedissonClient redissonClient;

    @EventListener(ApplicationReadyEvent.class)
    public void initRedis() {
        List<SeatSlots> slots = repository.findAll();
        for (SeatSlots slot : slots) {
            String key = "seatSlots:" + slot.getId();
            redissonClient.getBucket(key).set(slot.getAvailableSeats());
        }
    }
}
