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
import java.util.Comparator;
import java.util.List;


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
                    operatingDaysRepository.saveAndFlush(op);
                }
            }
        }
    }

    //레스토랑 운영일자 조회 테이블
    @Transactional
    public List<OperatingDaysResponseDto> getOperatingDays(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            OperatingDays existing = operatingDaysRepository.findByRestaurantAndDate(restaurant, date);
            if (existing == null) {
                OperatingDays op = OperatingDays.createForDate(restaurant, date);
                operatingDaysRepository.save(op);
            }
        }

        return operatingDaysRepository.findByRestaurant(restaurant).stream()
                .sorted(Comparator.comparing(
                        OperatingDays::getDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(op -> {
                    OperatingDaysResponseDto opDto = new OperatingDaysResponseDto();
                    opDto.setId(op.getId());
                    opDto.setDate(op.getDate());
                    opDto.setOperatingDate(op.getOperatingDate());
                    opDto.setOpen(op.isOpen());
                    return opDto;
                }).toList();

    }
}
