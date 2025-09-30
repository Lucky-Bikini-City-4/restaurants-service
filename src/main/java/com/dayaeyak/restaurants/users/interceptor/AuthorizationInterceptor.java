package com.dayaeyak.restaurants.users.interceptor;

import com.dayaeyak.restaurants.users.annotation.Authorize;
import com.dayaeyak.restaurants.users.dto.Passport;
import com.dayaeyak.restaurants.users.enums.UserRole;
import com.dayaeyak.restaurants.users.exception.CommonExceptionType;
import com.dayaeyak.restaurants.users.exception.CustomRuntimeException;
import com.dayaeyak.restaurants.users.service.AccessCheckService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final AccessCheckService accessCheckService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. HandlerMethod가 아니면 통과
        if (!(handler instanceof HandlerMethod method)) return true;

        // 2. @Authorize 어노테이션 확인
        Authorize authorize = method.getMethodAnnotation(Authorize.class);
        if (authorize == null) return true; // 어노테이션 없으면 모든 권한 허용
        log.info("Authorize annotation = {}", authorize);
        log.info("Authorize.roles = {}", Arrays.toString(authorize.roles()));
        log.info("Authorize.action = {}", authorize.action());
        log.info("Authorize.resourceId = {}", authorize.resourceId());
        log.info("Authorize.bypass = {}", authorize.bypass());


        // 3. 헤더에서 Passport 생성
        String userIdHeader = request.getHeader("X-User-Id");
        String userRoleHeader = request.getHeader("X-User-Role");

        if (userIdHeader == null || userRoleHeader == null) {
            log.warn("Missing X-User-Id or X-User-Role header");
            throw new CustomRuntimeException(CommonExceptionType.INVALID_USER_ID);
        }

        Passport passport;
        try {
            Long userId = Long.valueOf(userIdHeader);
            UserRole role = UserRole.valueOf(userRoleHeader.toUpperCase());
            passport = new Passport(userId, role);
            request.setAttribute("passport", passport); // 다른 곳에서 사용할 수 있도록
        } catch (Exception e) {
            log.error("Failed to parse Passport from headers", e);
            throw new CustomRuntimeException(CommonExceptionType.INVALID_USER_ID);
        }

        log.info("authorization interceptor passport: {}", passport);

        // 4. bypass가 true면 모든 권한 허용
        if (authorize.bypass()) return true;

        // 5. PathVariable 추출
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        Long objectId = null;
        if (!authorize.resourceId().isBlank()) {
            String pathValue = pathVariables.get(authorize.resourceId()); // ex) "performanceId"
            if (pathValue == null) {
                throw new CustomRuntimeException(CommonExceptionType.INVALID_RESOURCE_ID);
            }
            try {
                objectId = Long.valueOf(pathValue);
            } catch (NumberFormatException e) {
                log.warn("Invalid resourceId format for {}: {}", authorize.resourceId(), pathValue);
                throw new CustomRuntimeException(CommonExceptionType.INVALID_RESOURCE_ID);
            }

        }
        // 6. 권한 체크 (objectId는 AccessCheckService 내부에서 repo 조회 후 sellerId 매핑 가능)
        accessCheckService.checkPermission(passport, authorize.action(), authorize.roles(), objectId);

        return true;
    }
}