package com.dayaeyak.restaurants.restaurants.repository;

import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantRepositoryCustom {

    Optional<Restaurant> findByIdAndDeletedAtIsNull(Long id);

    Optional<Restaurant> findByApplicationIdAndDeletedAtIsNull(Long applicationId);

    @EntityGraph(attributePaths = "operatingDays")
    Optional<Restaurant> findWithOperatingDaysByIdAndDeletedAtIsNull(Long id);
}
