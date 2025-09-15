package com.dayaeyak.restaurants.operatingDays.service;

import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.operatingDays.dto.OperatingDaysResponseDto;
import com.dayaeyak.restaurants.operatingDays.entity.OperatingDays;
import com.dayaeyak.restaurants.operatingDays.repository.OperatingDaysRepository;
import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OperatingDaysService {

    private final OperatingDaysRepository operatingDaysRepository;
    private final RestaurantRepository restaurantRepository;

    // 매일 한 개씩 생성
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void dailyOperatingDays() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        for (Restaurant r : restaurants) {
            for(LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
                OperatingDays existing = operatingDaysRepository.findByRestaurantAndDate(r, date);
                if (existing == null) {
                    OperatingDays op = OperatingDays.createForDate(r, date);
                    operatingDaysRepository.save(op);
                }
            }
        }
    }

    //레스토랑 운영일자 조회 테이블
    @Transactional(readOnly = true)
    public List<OperatingDaysResponseDto> getOperatingDays(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        return restaurant.getOperatingDays().stream()
                .map(op -> {
                    OperatingDaysResponseDto dto = new OperatingDaysResponseDto();
                    dto.setDate(op.getDate());
                    dto.setOperatingDate(op.getOperatingDate());
                    dto.setOpen(op.isOpen());

                    //좌석 dto 매핑
                    List<OperatingDaysResponseDto.SeatDto> seatDtos = restaurant.getSeats().stream()
                            .filter(s -> s.getDate().equals(op.getDate()))
                            .map(s -> {
                                OperatingDaysResponseDto.SeatDto sd = new OperatingDaysResponseDto.SeatDto();
                                sd.setDate(s.getDate());
                                sd.setAvailableSeats(s.getAvailableSeats());
                                return sd;
                            }).collect(Collectors.toList());
                    dto.setSeats(seatDtos);
                    return dto;
                }).collect(Collectors.toList());

    }
}
