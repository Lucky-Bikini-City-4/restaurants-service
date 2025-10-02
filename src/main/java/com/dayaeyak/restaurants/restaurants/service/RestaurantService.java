package com.dayaeyak.restaurants.restaurants.service;


import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.common.responses.PageResponse;
import com.dayaeyak.restaurants.kafka.producer.RestaurantAlarmEvent;
import com.dayaeyak.restaurants.kafka.producer.dtos.ServiceRegisterRequestDto;
import com.dayaeyak.restaurants.kafka.producer.enums.ServiceType;
import com.dayaeyak.restaurants.kafka.producer.enums.Status;
import com.dayaeyak.restaurants.operatingDays.entity.OperatingDays;
import com.dayaeyak.restaurants.operatingDays.repository.OperatingDaysRepository;
import com.dayaeyak.restaurants.restaurants.dto.*;
import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.dayaeyak.restaurants.restaurants.repository.RestaurantRepository;
import com.dayaeyak.restaurants.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final OperatingDaysRepository operatingDaysRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final S3Service s3Service;

    private final String topic = "restaurants";


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
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto dto) {

        Restaurant restaurant = new Restaurant();
        restaurant.create(dto, s3Service);
        restaurantRepository.saveAndFlush(restaurant);

        for (OperatingDays day : restaurant.getOperatingDays()) {
            operatingDaysRepository.save(day);
            operatingDaysRepository.flush();
        }

        // 이벤트 등록 (트랜잭션 커밋 후 알람 전송)
        registerEvent(restaurant.getId(), dto.getSellerId(), dto.getName(), Status.CREATED);

        return restaurant.toResponseDto();
    }

    // 수정
    @Transactional
    public RestaurantResponseDto updateRestaurant(Long id, RestaurantRequestDto dto) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.update(dto, s3Service);
        restaurantRepository.save(restaurant);

        // 이벤트 등록 (트랜잭션 커밋 후 알람 전송)
        registerEvent(restaurant.getId(), dto.getSellerId(), dto.getName(), Status.UPDATED);

        return restaurant.toResponseDto();
    }

    // 삭제 (소프트 삭제)
    @Transactional
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.delete();

        // 이벤트 등록 (트랜잭션 커밋 후 알람 전송)
        registerEvent(restaurant.getId(), restaurant.getSellerId(), restaurant.getName(), Status.DELETED);
    }

    //활성화 상태 변환
    @Transactional
    public ActivationStatusDto changeActivation(Long id, ActivationRequestDto dto) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.setIsActivation(dto.getStatus());
        restaurantRepository.save(restaurant);

        return ActivationStatusDto.from(restaurant);
    }

    //웨이팅 상태 변환
    @Transactional
    public WaitingStatusDto changeWaiting(Long id, WaitingRequestDto dto) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.setWaitingActivation(dto.getStatus());
        restaurantRepository.save(restaurant);

        return WaitingStatusDto.from(restaurant);
    }

    //이벤트 등록
    private void registerEvent(Long restaurantId, Long userId, String restaurantName, Status status) {
        ServiceRegisterRequestDto dto = new ServiceRegisterRequestDto(
                userId,
                ServiceType.RESTAURANT,
                restaurantId,
                restaurantName,
                status
        );
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 이벤트 소스는 RestaurantService.this로 지정
                    eventPublisher.publishEvent(new RestaurantAlarmEvent(RestaurantService.this, topic, dto));
                }
            });
        } else {
            //트랜잭션 없으면 바로 발행(안전 장치)
            eventPublisher.publishEvent(new RestaurantAlarmEvent(this, topic, dto));
        }
    }
}
