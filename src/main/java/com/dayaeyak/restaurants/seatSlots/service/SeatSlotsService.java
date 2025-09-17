package com.dayaeyak.restaurants.seatSlots.service;


import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.seatSlots.dtos.SeatSlotDto;
import com.dayaeyak.restaurants.seatSlots.entity.SeatSlots;
import com.dayaeyak.restaurants.seatSlots.repository.SeatSlotsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SeatSlotsService {

    private final SeatSlotsRepository repository;

    @Transactional
    public SeatSlotDto reserveSlot(Long slotId, int count) {
        if (count < 1 || count > 4) {
            throw new BusinessException(ErrorCode.INVALID_SEAT_COUNT);
        }
        SeatSlots slot = repository.findById(slotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEATS_NOT_FOUND));
        if (slot.getAvailableSeats() < count) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_SEATS);
        }
        slot.setAvailableSeats(slot.getAvailableSeats() - count);
        repository.save(slot);

        SeatSlotDto dto = new SeatSlotDto();
        dto.setId(slot.getId());
        dto.setDate(slot.getDate());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setAvailableSeats(slot.getAvailableSeats());
        return dto;
    }

    @Transactional
    public List<SeatSlotDto> getSlots(Long restaurantId, Long operatingDayId) {
        List<SeatSlots> slots = repository.findByOperatingDay_IdAndOperatingDay_Restaurant_Id(operatingDayId, restaurantId);

        return slots.stream()
                .map(slot -> {
                    SeatSlotDto dto = new SeatSlotDto();
                    dto.setId(slot.getId());
                    dto.setDate(slot.getDate());
                    dto.setStartTime(slot.getStartTime());
                    dto.setEndTime(slot.getEndTime());
                    dto.setAvailableSeats(slot.getAvailableSeats());
                    return dto;
                }).collect(Collectors.toList());
    }
}
