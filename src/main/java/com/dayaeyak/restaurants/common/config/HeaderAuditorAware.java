package com.dayaeyak.restaurants.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class HeaderAuditorAware implements AuditorAware<String> {

    private static final String Header_User_Id = "X-User-Id";

    @Override
    public Optional<String> getCurrentAuditor() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes sra)) {
            return Optional.empty(); // 요청 스코프 바깥(배치/비동기) 일 때 -> HTTP요청이 없는 상황
        }
        HttpServletRequest request = sra.getRequest();
        String userId = request.getHeader(Header_User_Id);
        return Optional.ofNullable(userId);
    }
}
