package com.dayaeyak.restaurants.restaurants.controller;

import com.dayaeyak.restaurants.common.responses.ApiResponse;
import com.dayaeyak.restaurants.common.responses.PageResponse;
import com.dayaeyak.restaurants.common.security.AccessContext;
import com.dayaeyak.restaurants.common.security.UserRole;
import com.dayaeyak.restaurants.restaurants.dto.*;
import com.dayaeyak.restaurants.restaurants.enums.ActivationStatus;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.dayaeyak.restaurants.restaurants.enums.WaitingStatus;
import com.dayaeyak.restaurants.restaurants.service.RestaurantService;
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

    // 유저, 롤 확인
    @ModelAttribute
    public AccessContext setAccessContext(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        return AccessContext.of(userId, UserRole.of(role));
    }

    // 음식점 이름, 지역명으로 검색
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

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> getRestaurant(@PathVariable Long id) {
        RestaurantResponseDto dto = restaurantService.getRestaurant(id);
        return ApiResponse.success(HttpStatus.OK, "조회 성공", dto);
    }

    // 생성
    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> createRestaurant(
            @RequestBody RestaurantRequestDto requestDto,
            @ModelAttribute AccessContext ctx
    ) {
        RestaurantResponseDto dto = restaurantService.createRestaurant(requestDto, ctx);
        return ApiResponse.success(HttpStatus.CREATED, "음식점이 생성되었습니다.", dto);
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponseDto>> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantRequestDto requestDto,
            @ModelAttribute AccessContext ctx
    ) {
        RestaurantResponseDto dto = restaurantService.updateRestaurant(id, requestDto, ctx);
        return ApiResponse.success(HttpStatus.OK, "해당 음식점이 수정되었습니다.", dto);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(
            @PathVariable Long id,
            @ModelAttribute AccessContext ctx
    ) {
        restaurantService.deleteRestaurant(id, ctx);
        return ApiResponse.success(HttpStatus.NO_CONTENT, "해당 음식점이 삭제되었습니다.", null);
    }

    // 상태 변화 메서드

    @PostMapping("/{id}/activation")
    public ResponseEntity<ApiResponse<ActivationStatusDto>> changeActivation(
            @PathVariable Long id,
            @RequestBody ActivationRequestDto dto,
            @ModelAttribute AccessContext ctx
            ){
        ActivationStatusDto activationDto = restaurantService.changeActivation(id,dto,ctx);
        return ApiResponse.success(HttpStatus.OK, "음식점 활성화 상태가 변경되었습니다. ", activationDto);
    }

    @PostMapping("/{id}/waiting")
    public ResponseEntity<ApiResponse<WaitingStatusDto>> changeWaiting(
            @PathVariable Long id,
            @RequestBody WaitingRequestDto dto,
            @ModelAttribute AccessContext ctx
    ){
        WaitingStatusDto waitingDto = restaurantService.changeWaiting(id,dto,ctx);
        return  ApiResponse.success(HttpStatus.OK, "웨이팅 상태가 변경되었습니다. ", waitingDto);
    }

}
