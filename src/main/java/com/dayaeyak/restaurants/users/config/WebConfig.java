package com.dayaeyak.restaurants.users.config;

import com.dayaeyak.restaurants.users.interceptor.AuthorizationInterceptor;
import com.dayaeyak.restaurants.users.resolver.PassportHolderArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final PassportHolderArgumentResolver passportHolderArgumentResolver;

    private final AuthorizationInterceptor authorizationInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(passportHolderArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(authorizationInterceptor);
    }
}