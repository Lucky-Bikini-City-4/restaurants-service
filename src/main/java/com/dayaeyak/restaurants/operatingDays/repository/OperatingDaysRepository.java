package com.dayaeyak.restaurants.operatingDays.repository;

import com.dayaeyak.restaurants.operatingDays.entity.OperatingDays;
import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OperatingDaysRepository extends JpaRepository<OperatingDays, Long> {
    List<OperatingDays> findByRestaurant(Restaurant restaurant);

    OperatingDays findByRestaurantAndDate(Restaurant restaurant, LocalDate date);
}
