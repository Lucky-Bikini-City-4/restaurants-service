package com.dayaeyak.restaurants.users.resolver;

import com.dayaeyak.restaurants.users.annotation.PassportHolder;
import com.dayaeyak.restaurants.users.dto.Passport;
import com.dayaeyak.restaurants.users.enums.UserRole;
import com.dayaeyak.restaurants.users.exception.CommonExceptionType;
import com.dayaeyak.restaurants.users.exception.CustomRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
@RequiredArgsConstructor
public class PassportHolderArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PassportHolder.class)
                && parameter.getParameterType().equals(Passport.class);
    }

    @Override
    public Passport resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String id = request.getHeader(USER_ID_HEADER);
        String role = request.getHeader(USER_ROLE_HEADER);

        if (!StringUtils.hasText(id)) {
            throw new CustomRuntimeException(CommonExceptionType.INVALID_USER_ID);
        }

        if (!StringUtils.hasText(role)) {
            throw new CustomRuntimeException(CommonExceptionType.INVALID_USER_ROLE);
        }

        Passport passport = new Passport(
                Long.valueOf(id),
                UserRole.of(role)
        );

        // request에 세팅 → 인터셉터에서 재사용 가능
        request.setAttribute("passport", passport);
        return passport;
    }
}

