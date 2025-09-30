package com.dayaeyak.restaurants.users.service;

import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.common.security.AccessGuard;
import com.dayaeyak.restaurants.common.security.Action;
import com.dayaeyak.restaurants.common.security.ResourceScope;
import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.repository.RestaurantRepository;
import com.dayaeyak.restaurants.users.dto.Passport;
import com.dayaeyak.restaurants.users.enums.UserRole;
import com.dayaeyak.restaurants.users.exception.CommonExceptionType;
import com.dayaeyak.restaurants.users.exception.CustomRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessCheckService {

    private final RestaurantRepository repository;

    public void checkPermission(Passport passport, Action action, UserRole[] roles, Long objectId) {
        ResourceScope scope = null;
        if (objectId != null) {
            Restaurant restaurant = repository.findByIdAndDeletedAtIsNull(objectId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
            log.info("Application found: id={}, sellerId={}", restaurant.getId(), restaurant.getSellerId());

            // sellerId null이면 바로 예외 발생
            if (restaurant.getSellerId() == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "해당 신청서의 판매자 아이디가 필요합니다.");
            }
            scope = ResourceScope.of(restaurant.getSellerId());
            log.info("ResourceScope set with sellerId={}", restaurant.getSellerId());
        }

        // roles 필터링: 필요하면 AccessGuard 내부에서 검사
        if (roles.length != 0 && Arrays.stream(roles).noneMatch(r -> r == passport.role())) {
            throw new CustomRuntimeException(CommonExceptionType.REQUEST_ACCESS_DENIED);
        }
        log.info("Calling AccessGuard.requiredPermission - action={}, userId={}, role={}, scope={}",
                action, passport.userId(), passport.role(), scope);
        AccessGuard.requiredPermission(action, passport.userId(), passport.role(), Optional.ofNullable(scope));
    }
}
