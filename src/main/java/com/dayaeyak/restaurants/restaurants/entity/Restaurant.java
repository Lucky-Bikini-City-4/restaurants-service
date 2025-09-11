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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name= "restaurants")
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =  "yyyy-MM-dd'T'HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =  "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    // 잔여좌석 테이블 -> 자동 생성/삭제/수정 가능
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seats> seats;

    // 영업일 테이블 -> 요일별 운영 정보 관리
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperatingDays> operatingDays;

    // CRUD 메서드

    //생성
    public void create(RestaurantRequestDto dto, Long userId) {
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

        // 잔여좌석 기본 생성
        Seats seat = new Seats();
        seat.setDate(LocalDate.now());
        seat.setAvailableSeats(this.capacity);
        seat.setRestaurant(this);
        this.seats.add(seat);

        // 운영 일자 생성
        for (ClosedDays day : ClosedDays.values()) {
            OperatingDays op = new OperatingDays();
            op.setDayOfWeek(day);
            op.setOpen(!day.equals(this.closedDay));
            this.operatingDays.add(op);
        }
    }

    //수정
    public void update(RestaurantRequestDto dto) {
        this.name = dto.getName();
        this.address = dto.getAddress();
        this.phoneNumber = dto.getPhoneNumber();
        this.closedDay = dto.getClosedDay();
        this.openTime = dto.getOpenTime();
        this.closeTime = dto.getCloseTime();
        this.type = dto.getType();
        this.capacity = dto.getCapacity();
        this.isActivation = dto.getIsActivation();
        this.waitingActivation = dto.getWaitingActivation();

        // 잔여좌석 수정 반영
        if (this.seats != null) {
            this.seats.forEach(seat -> seat.setRestaurant(this));
        }

        // 운영일자 동기화
        if (this.operatingDays != null) {
            this.operatingDays.forEach(op ->{
                if (op.getDayOfWeek().equals(this.closedDay)) {
                    op.setOpen(false);
                }else{
                    op.setOpen(true);
                }
            });
        }
    }

    // 소프트 삭제
    public void delete(){
        this.deletedAt = LocalDateTime.now();

        if(this.seats != null){
            this.seats.forEach(s -> s.setDeletedAt(LocalDateTime.now()));
        }
        if(this.operatingDays != null){
            this.operatingDays.forEach(s -> s.setDeletedAt(LocalDateTime.now()));
        }
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
