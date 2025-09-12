package com.dayaeyak.restaurants.seats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "seat_restore_tasks")
public class SeatRestoreTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seatId;

    private int restoreCount;

    private boolean executed = false;

    private LocalDateTime executeAt;

}
