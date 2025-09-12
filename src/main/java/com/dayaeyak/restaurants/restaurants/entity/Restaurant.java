package com.dayaeyak.restaurants.restaurants.entity;


import com.dayaeyak.restaurants.operatingDays.entity.OperatingDays;
import com.dayaeyak.restaurants.restaurants.dto.RestaurantRequestDto;
import com.dayaeyak.restaurants.restaurants.dto.RestaurantResponseDto;
import com.dayaeyak.restaurants.restaurants.enums.ActivationStatus;
import com.dayaeyak.restaurants.restaurants.enums.ClosedDays;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.dayaeyak.restaurants.restaurants.enums.WaitingStatus;
import com.dayaeyak.restaurants.seats.entity.Seats;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "restaurants")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE restaurants SET deleted_at= NOW()")  // 소프트삭제 구현: delete 호출 시 실제 row는 남기고 deleted_at 컬럼만 갱신
@Where(clause = "deleted_at IS NULL")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long sellerId;

    private String address;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ClosedDays closedDay;       // 기본 휴무일

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;        // 영업 시작 시간

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;      // 영업 종료 시간

    @Enumerated(EnumType.STRING)
    private RestaurantType type;

    private int capacity;              // 총 좌석 수

    @Enumerated(EnumType.STRING)
    private ActivationStatus isActivation;  // 영업 활성 상태

    private String city;

    @Enumerated(EnumType.STRING)
    private WaitingStatus waitingActivation;   // 웨이팅 사용 유무

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 잔여좌석 테이블 -> 자동 생성/삭제/수정 가능
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seats> seats = new HashSet<>();

    // 영업일 테이블 -> 요일별 운영 정보 관리
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OperatingDays> operatingDays = new HashSet<>();

    // CRUD 메서드

    //생성
    public void create(RestaurantRequestDto dto, Long userId) {
        setBasicInfo(dto, userId);
        generateOperatingDaysAndSeats(30); // 등록 시 미래 30일까지 자동 생성
    }

    //수정
    public void update(RestaurantRequestDto dto) {
        setBasicInfo(dto, this.sellerId);
        generateOperatingDaysAndSeats(30); // 수정 시 미래 30일까지 동기화
    }

    private void setBasicInfo(RestaurantRequestDto dto, Long userId) {
        this.name = dto.getName();
        this.sellerId = userId;
        this.address = dto.getAddress();
        this.phoneNumber = dto.getPhoneNumber();
        this.closedDay = dto.getClosedDay();
        this.openTime = dto.getOpenTime();
        this.closeTime = dto.getCloseTime();
        this.type = dto.getType();
        this.capacity = dto.getCapacity();
        this.isActivation = dto.getIsActivation();
        this.waitingActivation = dto.getWaitingActivation();
    }

    // 소프트 삭제
    public void delete() {
        this.deletedAt = LocalDateTime.now();

        if (this.seats != null) {
            this.seats.forEach(s -> s.setDeletedAt(LocalDateTime.now()));
        }
        if (this.operatingDays != null) {
            this.operatingDays.forEach(s -> s.setDeletedAt(LocalDateTime.now()));
        }
    }

    public void generateOperatingDaysAndSeats(int daysAhead) {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < daysAhead; i++) {
            LocalDate date = today.plusDays(i);
            upsertOperatingDayAndSeat(date);
        }
    }

    private void upsertOperatingDayAndSeat(LocalDate date) {
        ClosedDays dayOfWeek = ClosedDays.fromDayOfWeek(date.getDayOfWeek());

        // 운영일 가져오기 또는 생성
        OperatingDays op = operatingDays.stream()
                .filter(o -> o.getDate().equals(date))
                .findFirst()
                .orElseGet(() -> {
                    OperatingDays newOp = new OperatingDays();
                    newOp.setRestaurant(this);
                    operatingDays.add(newOp);
                    return newOp;
                });

        op.setDate(date);
        op.setOperatingDate(dayOfWeek);
        op.setOpen(!dayOfWeek.equals(this.closedDay));

        // 좌석 가져오기 또는 생성
        Seats seat = seats.stream()
                .filter(s -> s.getDate().equals(date))
                .findFirst()
                .orElseGet(() -> {
                    Seats newSeat = new Seats();
                    newSeat.setRestaurant(this);
                    seats.add(newSeat);
                    return newSeat;
                });

        seat.setDate(date);
        seat.setAvailableSeats(this.capacity);
    }

    public RestaurantResponseDto toResponseDto() {
        RestaurantResponseDto dto = new RestaurantResponseDto();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setSellerId(this.sellerId);
        dto.setAddress(this.address);
        dto.setPhoneNumber(this.phoneNumber);
        dto.setClosedDay(this.closedDay);
        dto.setOpenTime(this.openTime);
        dto.setCloseTime(this.closeTime);
        dto.setType(this.type);
        dto.setCapacity(this.capacity);
        dto.setIsActivation(this.isActivation);
        dto.setWaitingActivation(this.waitingActivation);
        return dto;
    }
}
