package com.dayaeyak.restaurants.common.security;

import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessGuard {

    public static void requiredPermission(Action action, AccessContext ctx, ResourceScope scope) {
        if (ctx == null || ctx.getRole() == null) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "인증정보가 없습니다.");
        }

        switch (ctx.getRole()) {
            case MASTER -> checkMaster(action);
            case SELLER -> checkSeller(action, ctx, scope);
            case NORMAL -> checkNormal(action);
            default -> throw new BusinessException(ErrorCode.ACCESS_DENIED, "허용되지 않은 역할입니다.");
        }
    }

    // MASTER는 모든 권한 허용
    private static void checkMaster(Action action) {
        // 아무것도 하지 않음
    }

    // SELLER 권한 검증
    private static void checkSeller(Action action, AccessContext ctx, ResourceScope scope) {
        requireSeller(scope);

        if (!Objects.equals(ctx.getUserId(), scope.getSellerId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "등록된 판매관리자만 접근할 수 있습니다.");
        }

        if (action != Action.UPDATE) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "판매자는 수정만 가능합니다.");
        }
    }

    // NORMAL 권한 검증
    private static void checkNormal(Action action) {
        if (action != Action.READ) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "일반 사용자는 조회만 가능합니다.");
        }
    }

    // scope 검증
    private static void requireSeller(ResourceScope scope) {
        if (scope == null || scope.getSellerId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "등록된 판매자 아이디가 필요합니다.");
        }
    }
}
