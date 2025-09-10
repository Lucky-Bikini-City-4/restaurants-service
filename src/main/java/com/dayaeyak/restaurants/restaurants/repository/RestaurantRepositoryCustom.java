package com.dayaeyak.restaurants.restaurants.repository;

import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {
    Page<Restaurant> SearchByNameOrCity(String name, String city, RestaurantType type, Pageable pageable);
}
