package com.dayaeyak.restaurants.seatSlots.repository;

import com.dayaeyak.restaurants.seatSlots.entity.SeatSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatSlotsRepository extends JpaRepository<SeatSlots, Long> {
    List<SeatSlots> findByOperatingDay_IdAndOperatingDay_Restaurant_Id(Long operatingDayId, Long restaurantId);
    Optional<SeatSlots> findByIdAndOperatingDay_IdAndOperatingDay_Restaurant_Id(
            Long slotId, Long operatingDayId, Long restaurantId
    );
}
