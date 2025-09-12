package com.dayaeyak.restaurants.restaurants.repository;

import com.dayaeyak.restaurants.restaurants.entity.QRestaurant;
import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Restaurant> SearchByNameOrCity(String name, String city, RestaurantType type, Pageable pageable) {

        QRestaurant restaurant = QRestaurant.restaurant;
        BooleanBuilder builder = new BooleanBuilder();

        // 소프트 삭제 조건
        builder.and(restaurant.deletedAt.isNull());

        // 가게명 검색
        if (name != null && !name.isBlank()) {
            builder.and(restaurant.name.containsIgnoreCase(name));
        }

        // 도시 검색
        if (city != null && !city.isBlank()) {
            builder.and(restaurant.city.containsIgnoreCase(city));
        }

        // 음식점 타입 검색
        if (type != null) {
            builder.and(restaurant.type.eq(type));
        }

        List<Restaurant> results = queryFactory.
                selectFrom(restaurant).distinct()
                .leftJoin(restaurant.seats).fetchJoin()    // <- N+1 문제, LazyInitializationException 예방 가능
                .leftJoin(restaurant.operatingDays).fetchJoin()
                .where(builder)              // builder에 담긴 조건들을 한꺼번에 WHERE 절로 적용
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable, restaurant))
                .fetch();

        // 전체 count 조회
        Long total = queryFactory
                .select(restaurant.count())
                .from(restaurant)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable, QRestaurant restaurant) {
        if (pageable.getSort().isUnsorted()) return new OrderSpecifier<?>[0];

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order sort : pageable.getSort()) {
            com.querydsl.core.types.Order direction = sort.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
            switch (sort.getProperty()) {
                case "name":
                    orders.add(new OrderSpecifier<>(direction, restaurant.name));
                    break;
                case "city":
                    orders.add(new OrderSpecifier<>(direction, restaurant.city));
                    break;
                case "capacity":
                    orders.add(new OrderSpecifier<>(direction, restaurant.capacity));
                    break;
                default:
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[orders.size()]);  //orderBy는 가변인자이므로 list를 array로 변환하여야 함.
    }
}
