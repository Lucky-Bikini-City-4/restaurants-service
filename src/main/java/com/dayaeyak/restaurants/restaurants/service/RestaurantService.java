package com.dayaeyak.restaurants.restaurants.service;


import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.common.responses.PageResponse;
import com.dayaeyak.restaurants.common.security.AccessContext;
import com.dayaeyak.restaurants.common.security.AccessGuard;
import com.dayaeyak.restaurants.common.security.Action;
import com.dayaeyak.restaurants.common.security.ResourceScope;
import com.dayaeyak.restaurants.operatingDays.entity.OperatingDays;
import com.dayaeyak.restaurants.operatingDays.repository.OperatingDaysRepository;
import com.dayaeyak.restaurants.restaurants.dto.*;
import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.dayaeyak.restaurants.restaurants.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final OperatingDaysRepository operatingDaysRepository;

    // 조건 기반 조회
    @Transactional(readOnly = true)
    public PageResponse<RestaurantResponseDto> searchRestaurants(
            String name, String city, RestaurantType type, Pageable pageable
    ) {
        Page<Restaurant> page = restaurantRepository.SearchByNameOrCity(name, city, type, pageable);
        Page<RestaurantResponseDto> dtoPage = page.map(Restaurant::toResponseDto);
        return PageResponse.of(dtoPage);
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public RestaurantResponseDto getRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        return restaurant.toResponseDto();
    }

    // 생성
    @Transactional
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto dto, AccessContext ctx) {
        AccessGuard.requiredPermission(Action.CREATE, ctx, ResourceScope.of(ctx.getUserId()));
        Restaurant restaurant = new Restaurant();
        restaurant.create(dto, ctx.getUserId());

        restaurantRepository.save(restaurant);
        restaurantRepository.flush();

        for(OperatingDays day : restaurant.getOperatingDays()) {
            operatingDaysRepository.save(day);
            operatingDaysRepository.flush();
        }

        return restaurant.toResponseDto();
    }

    // 수정
    @Transactional
    public RestaurantResponseDto updateRestaurant(Long id, RestaurantRequestDto dto, AccessContext ctx) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        AccessGuard.requiredPermission(Action.UPDATE, ctx, ResourceScope.of(restaurant.getSellerId()));
        restaurant.update(dto);
        restaurantRepository.save(restaurant);
        return restaurant.toResponseDto();
    }

    // 삭제 (소프트 삭제)
    @Transactional
    public void deleteRestaurant(Long id, AccessContext ctx) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        AccessGuard.requiredPermission(Action.DELETE, ctx, ResourceScope.of(restaurant.getSellerId()));
        restaurant.delete();
        restaurantRepository.save(restaurant);
    }

    //활성화 상태 변환
    @Transactional
    public ActivationStatusDto changeActivation(Long id, ActivationRequestDto dto, AccessContext ctx) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        AccessGuard.requiredPermission(Action.UPDATE, ctx, ResourceScope.of(restaurant.getSellerId()));
        restaurant.setIsActivation(dto.getStatus());
        restaurantRepository.save(restaurant);

        return  ActivationStatusDto.from(restaurant);
    }


    //웨이팅 상태 변환

    @Transactional
    public WaitingStatusDto changeWaiting(Long id, WaitingRequestDto dto, AccessContext ctx) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        AccessGuard.requiredPermission(Action.UPDATE, ctx, ResourceScope.of(restaurant.getSellerId()));
        restaurant.setWaitingActivation(dto.getStatus());
        restaurantRepository.save(restaurant);

        return  WaitingStatusDto.from(restaurant);
    }
}
