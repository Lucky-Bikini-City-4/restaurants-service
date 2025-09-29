package com.dayaeyak.restaurants.restaurants.entity;


import com.dayaeyak.restaurants.operatingDays.entity.OperatingDays;
import com.dayaeyak.restaurants.restaurants.dto.RestaurantRequestDto;
import com.dayaeyak.restaurants.restaurants.dto.RestaurantResponseDto;
import com.dayaeyak.restaurants.restaurants.enums.ActivationStatus;
import com.dayaeyak.restaurants.restaurants.enums.ClosedDays;
import com.dayaeyak.restaurants.restaurants.enums.RestaurantType;
import com.dayaeyak.restaurants.restaurants.enums.WaitingStatus;
import com.dayaeyak.restaurants.s3.S3Service;
import com.dayaeyak.restaurants.seatSlots.entity.SeatSlots;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Setter
@Entity
@Table(name = "restaurants")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE restaurants SET deleted_at= NOW() where id =?")  // 소프트삭제 구현: delete 호출 시 실제 row는 남기고 deleted_at 컬럼만 갱신
@Where(clause = "deleted_at IS NULL")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    private String name;

    private String imageUrl;

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

    // 영업일 테이블 -> 요일별 운영 정보 관리
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OperatingDays> operatingDays = new HashSet<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SeatSlots> slots = new HashSet<>();

    // CRUD 메서드

    //생성
    public void create(RestaurantRequestDto dto, S3Service s3Service) {
        setBasicInfo(dto, s3Service);
        generateOperatingDaysAndSeats(30); // 등록 시 미래 30일까지 자동 생성
    }

    //수정
    public void update(RestaurantRequestDto dto, S3Service s3Service) {
        setBasicInfo(dto, s3Service);
        regenerateOPAndSeats(30); // 수정 시 미래 30일까지 동기화
    }

    private void setBasicInfo(RestaurantRequestDto dto, S3Service s3Service) {
        this.applicationId = dto.getApplicationId();
        this.name = dto.getName();
        //이미지 업로드
        if(dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            try{
                this.imageUrl = s3Service.uploadFile(dto.getImageFile());
            }catch(IOException e){
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
        this.sellerId = dto.getSellerId();
        this.address = dto.getAddress();
        this.phoneNumber = dto.getPhoneNumber();
        this.closedDay = dto.getClosedDay();
        this.openTime = dto.getOpenTime();
        this.closeTime = dto.getCloseTime();
        this.type = dto.getType();
        this.capacity = dto.getCapacity();
        this.city = dto.getCity();

        // activation, waiting 초기화 값 추가
        this.isActivation = (dto.getIsActivation() != null)
                ? dto.getIsActivation()
                : ActivationStatus.ON;
        this.waitingActivation = (dto.getWaitingActivation() != null)
                ? dto.getWaitingActivation()
                : WaitingStatus.ON;
    }

    // 소프트 삭제
    public void delete() {
        this.deletedAt = LocalDateTime.now();

        if (this.slots != null) {
            this.slots.forEach(s -> s.setDeletedAt(LocalDateTime.now()));
        }
        if (this.operatingDays != null) {
            this.operatingDays.forEach(s -> s.setDeletedAt(LocalDateTime.now()));
        }
    }

    // 잔여좌석, 운영일자 전체 초기화
    public void regenerateOPAndSeats(int daysAhead) {
        LocalDate today = LocalDate.now();
        operatingDays.removeIf(o ->{
            if(!o.getDate().isBefore(today)) {
                if(o.getSeatSlots() != null){
                    log.info("기존 잔여좌석 삭제 - 음식점 ID: {}", this.getId());
                    o.getSeatSlots().clear();
                }
                log.info("기존 운영일 삭제 - 음식점Id: {}", this.getId());
                return true;
            }
            log.info("오늘{} 이전 데이터는 유지", LocalDate.now());
            return false;
        });

        generateOperatingDaysAndSeats(daysAhead);
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

        // 운영일 열려있으면 slot 생성
        if (op.isOpen()) {
            if (this.openTime == null || this.closeTime == null) {
                log.warn("영업 시작/종료 시간이 null입니다. 레스토랑ID: {}, 날짜: {} - slot 생성 스킵", this.getId(), date);
                return;
            }
            try {
                op.generateSeatSlots(this.capacity, this.openTime, this.closeTime);
            } catch (Exception e) {
                log.error("Slot 생성 실패 - 레스토랑ID: {}, 날짜: {}", this.getId(), date, e);
                throw new RuntimeException("Slot 생성 실패", e); // 트랜잭션 롤백
            }
        }
    }

    public RestaurantResponseDto toResponseDto() {
        RestaurantResponseDto dto = new RestaurantResponseDto();
        dto.setId(this.id);
        dto.setApplicantId(this.applicationId);
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
