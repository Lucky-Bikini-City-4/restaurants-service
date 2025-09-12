package com.dayaeyak.restaurants.seats.repository;

import com.dayaeyak.restaurants.seats.entity.SeatRestoreTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatsRestoreTaskRepository extends JpaRepository<SeatRestoreTask, Long> {
    List<SeatRestoreTask> findByExecutedFalseAndExecutedBefore(LocalDateTime date);
}
