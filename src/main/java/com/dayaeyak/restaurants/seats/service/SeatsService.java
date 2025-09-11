package com.dayaeyak.restaurants.seats.service;

import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.seats.dto.SeatAvailabilityDto;
import com.dayaeyak.restaurants.seats.entity.Seats;
import com.dayaeyak.restaurants.seats.repository.SeatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SeatsService {

    private final SeatsRepository seatsRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Transactional
    public SeatAvailabilityDto reserveSeats(Long restaurantId, LocalDate date, int count){
        Seats seats = seatsRepository.findByRestaurantIdAndDate(restaurantId, date)
                .stream()
                .findFirst()
                .orElseThrow(()-> new BusinessException(ErrorCode.SEATS_NOT_FOUND));
        if(seats.getAvailableSeats() < count){
            throw new BusinessException(ErrorCode.INSUFFICIENT_SEATS);
        }

        // 좌석 차감
        seats.setAvailableSeats(seats.getAvailableSeats() - count);
        seatsRepository.save(seats);

        // 1시간 30분 뒤 차감된 좌석 복구
        scheduler.schedule(()-> restoreSeats(seats.getId(), count), 90, TimeUnit.MINUTES);

        SeatAvailabilityDto dto  = new SeatAvailabilityDto();
        dto.setDate(seats.getDate());
        dto.setAvailableSeats(seats.getAvailableSeats());
        return dto;

    }

    @Transactional
    public void restoreSeats(Long setId, int count){
        seatsRepository.findById(setId).ifPresent(seat -> {
            seat.setAvailableSeats(seat.getAvailableSeats() + count);
            seatsRepository.save(seat);
        });
    }

    @Transactional(readOnly = true)
    public SeatAvailabilityDto getSeats(Long restaurantId, LocalDate date) {
        Seats seat = seatsRepository.findByRestaurantIdAndDate(restaurantId, date)
                .stream()
                .findFirst()
                .orElseThrow(()-> new BusinessException(ErrorCode.SEATS_NOT_FOUND));
        SeatAvailabilityDto dto = new SeatAvailabilityDto();
        dto.setDate(seat.getDate());
        dto.setAvailableSeats(seat.getAvailableSeats());
        return dto;
    }

}
