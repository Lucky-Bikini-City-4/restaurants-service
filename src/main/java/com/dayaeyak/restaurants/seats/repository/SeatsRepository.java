package com.dayaeyak.restaurants.seats.repository;

import com.dayaeyak.restaurants.seats.entity.Seats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeatsRepository extends JpaRepository<Seats, Long> {

    // 모든 좌석 조회
    List<Seats> findByRestaurantId(Long restaurantId);

    // 특정 날짜 좌석 조회
    List<Seats> findByRestaurantIdAndDate(Long restaurantId, LocalDate date);

}
