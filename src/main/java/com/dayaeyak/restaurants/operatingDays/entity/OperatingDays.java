package com.dayaeyak.restaurants.operatingDays.entity;

import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.enums.ClosedDays;
import com.dayaeyak.restaurants.seats.entity.Seats;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "operating_days")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE operating_days SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class OperatingDays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; // 오늘 날짜

    @Enumerated(EnumType.STRING)
    private ClosedDays operatingDate; // 현재 요일

    private boolean isOpen; // 해당 요일 운영 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")  // 연관된 음식점
    private Restaurant restaurant;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 오늘 날짜 기준으로 OperatingDays 생성
    public static OperatingDays createForDate(Restaurant restaurant, LocalDate date) {
        OperatingDays op = new OperatingDays();
        op.setRestaurant(restaurant);
        op.setDate(date);

        ClosedDays day = ClosedDays.fromDayOfWeek(DayOfWeek.from(date));
        op.setOperatingDate(day);
        op.setOpen(!day.equals(restaurant.getClosedDay()));

        //seats 생성
        Seats seat = new Seats();
        seat.setRestaurant(restaurant);
        seat.setDate(date);
        seat.setAvailableSeats(restaurant.getCapacity());
        restaurant.getSeats().add(seat);

        restaurant.getOperatingDays().add(op);

        return op;
    }

}
