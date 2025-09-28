package com.dayaeyak.restaurants.users.annotation;

import com.dayaeyak.restaurants.users.enums.UserRole;
import com.dayaeyak.restaurants.common.security.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Authorize {

    UserRole[] roles() default {};  // 허용되는 롤
    boolean bypass() default false;  // 강제로 검증 우회
    boolean checkOwner() default false; //리소스 소유권 검증 여부
    Action action() default Action.NONE;
    String resourceId() default "";

}

