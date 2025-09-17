package com.dayaeyak.restaurants.seatSlots.entity;

import com.dayaeyak.restaurants.operatingDays.entity.OperatingDays;
import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "seat_slots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"operating_day_id", "start_time"}))
@SQLDelete(sql = "UPDATE seat_slots SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class SeatSlots {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operating_day_id")
    private OperatingDays operatingDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int availableSeats;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
