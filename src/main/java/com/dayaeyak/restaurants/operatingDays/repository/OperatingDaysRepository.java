package com.dayaeyak.restaurants.operatingDays.repository;

import com.dayaeyak.restaurants.operatingDays.entity.OperatingDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperatingDaysRepository extends JpaRepository<OperatingDays, Long> {
    List<OperatingDays> findByRestaurantId(Long restaurantId);
}
