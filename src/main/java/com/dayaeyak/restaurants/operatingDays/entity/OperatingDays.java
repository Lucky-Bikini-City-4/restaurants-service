package com.dayaeyak.restaurants.operatingDays.entity;

import com.dayaeyak.restaurants.restaurants.entity.Restaurant;
import com.dayaeyak.restaurants.restaurants.enums.ClosedDays;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @Enumerated(EnumType.STRING)
    private ClosedDays dayOfWeek; // 요일

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
}
