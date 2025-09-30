package com.dayaeyak.restaurants.common.security;

import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.users.enums.UserRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessGuard {

    public static void requiredPermission(Action action, Long userId, UserRole role, Optional<ResourceScope> scope) {
        if  (userId == null || role == null) {
            log.warn("[ACCESS DENIED] userId={}, role={}, action={}, reason={}", userId, role, action, "인증 정보 없음");
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "인증정보가 없습니다.");
        }

        switch (role) {
            case MASTER -> checkMaster(action);
            case SELLER -> checkSeller(action, userId, scope);
            case NORMAL -> checkNormal(action);
            default -> throw new BusinessException(ErrorCode.ACCESS_DENIED, "허용되지 않은 역할입니다.");
        }
    }

    // MASTER는 모든 권한 허용
    private static void checkMaster(Action action) {
        // 아무것도 하지 않음
    }

    // SELLER 권한 검증
    private static void checkSeller(Action action, Long userId, Optional<ResourceScope> scopeOpt) {
        if (action == Action.CREATE) {
            // CREATE일 때는 신규 리소스라 scope 필요 없음
            return;
        }
        ResourceScope scope = scopeOpt.orElseThrow(() ->
                new BusinessException(ErrorCode.INVALID_INPUT, "판매자 리소스 스코프가 필요합니다.")
        );

        if (!Objects.equals(userId, scope.getSellerId())) {
            log.warn("[ACCESS DENIED] userId={}, sellerId={}, action={}, reason={}", userId, scope.getSellerId(), action, "소유권 불일치");
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "등록된 판매자만 접근할 수 있습니다.");
        }

        if (action != Action.UPDATE && action != Action.READ) {
            log.warn("[ACCESS DENIED] userId={}, sellerId={}, action={}, reason={}", userId, scope.getSellerId(), action, "허용되지 않은 작업");
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "판매자는 소속 음식점 정보를 생성 수정만 가능합니다.");
        }
    }

    // NORMAL 권한 검증
    private static void checkNormal(Action action) {
        if (action != Action.READ) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "일반 사용자는 조회만 가능합니다.");
        }
    }
}
