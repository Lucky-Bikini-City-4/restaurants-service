package com.dayaeyak.restaurants.restaurants.controller;

import com.dayaeyak.restaurants.common.responses.ApiResponse;
import com.dayaeyak.restaurants.common.responses.PageResponse;
import com.dayaeyak.restaurants.common.security.Action;
import com.dayaeyak.restaurants.restaurants.dto.*;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.dayaeyak.restaurants.restaurants.service.RestaurantService;
import com.dayaeyak.restaurants.users.annotation.Authorize;
import com.dayaeyak.restaurants.users.annotation.PassportHolder;
import com.dayaeyak.restaurants.users.dto.Passport;
import com.dayaeyak.restaurants.users.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/restaurants")
@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 음식점 이름, 지역명으로 검색, 비회원도 조회 가능
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RestaurantResponseDto>>> searchRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) RestaurantType type,
            Pageable pageable
    ) {
        PageResponse<RestaurantResponseDto> response = restaurantService.searchRestaurants(name, city, type, pageable);
        return ApiResponse.success(HttpStatus.OK, "조회 성공", response);
    }

    // 단건 조회 , 비회원도 조회 가능
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> getRestaurant(@PathVariable Long id) {
        RestaurantResponseDto dto = restaurantService.getRestaurant(id);
        return ApiResponse.success(HttpStatus.OK, "조회 성공", dto);
    }

    // 생성
    @PostMapping
    @Authorize(roles = {UserRole.MASTER}, action = Action.CREATE)
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> createRestaurant(
            @RequestBody RestaurantRequestDto requestDto,
            @PassportHolder Passport passport
    ) {
        System.out.println("REQ BODY: " + requestDto);
        RestaurantResponseDto dto = restaurantService.createRestaurant(requestDto);
        return ApiResponse.success(HttpStatus.CREATED, "음식점이 생성되었습니다.", dto);
    }

    // 수정
    @PutMapping("/{id}")
    @Authorize(roles = {UserRole.MASTER, UserRole.SELLER}, checkOwner = true, action = Action.UPDATE, resourceId = "id")
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantRequestDto requestDto,
            @PassportHolder Passport passport
    ) {
        RestaurantResponseDto dto = restaurantService.updateRestaurant(id, requestDto);
        return ApiResponse.success(HttpStatus.OK, "해당 음식점이 수정되었습니다.", dto);
    }

    // 삭제
    @DeleteMapping("/{id}")
    @Authorize(roles = {UserRole.MASTER}, action = Action.DELETE, resourceId = "id")
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(
            @PathVariable Long id,
            @PassportHolder Passport passport
    ) {
        restaurantService.deleteRestaurant(id);
        return ApiResponse.success(HttpStatus.NO_CONTENT, "해당 음식점이 삭제되었습니다.", null);
    }

    // 상태 변화 메서드

    @PostMapping("/{id}/activation")
    @Authorize(roles = {UserRole.MASTER, UserRole.SELLER}, checkOwner = true, action = Action.UPDATE, resourceId = "id")
    public ResponseEntity<ApiResponse<ActivationStatusDto>> changeActivation(
            @PathVariable Long id,
            @RequestBody ActivationRequestDto dto,
            @PassportHolder Passport passport
    ) {
        ActivationStatusDto activationDto = restaurantService.changeActivation(id, dto);
        return ApiResponse.success(HttpStatus.OK, "음식점 활성화 상태가 변경되었습니다. ", activationDto);
    }

    @PostMapping("/{id}/waiting")
    @Authorize(roles = {UserRole.MASTER, UserRole.SELLER}, checkOwner = true, action = Action.UPDATE, resourceId = "id")
    public ResponseEntity<ApiResponse<WaitingStatusDto>> changeWaiting(
            @PathVariable Long id,
            @RequestBody WaitingRequestDto dto,
            @PassportHolder Passport passport
    ) {
        WaitingStatusDto waitingDto = restaurantService.changeWaiting(id, dto);
        return ApiResponse.success(HttpStatus.OK, "웨이팅 상태가 변경되었습니다. ", waitingDto);
    }

}
