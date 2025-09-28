package com.dayaeyak.restaurants.seatSlots.service;


import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.seatSlots.dtos.SeatSlotDto;
import com.dayaeyak.restaurants.seatSlots.entity.SeatSlots;
import com.dayaeyak.restaurants.seatSlots.repository.SeatSlotsRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SeatSlotsService {

    private final SeatSlotsRepository repository;
    private final RedissonClient redissonClient;

    private String loadLuaScript() {  //InputStream은 '외부데이터 -> 바이트 단위'로 읽어오는 추상 클래스
        try(InputStream is = getClass().getResourceAsStream("/lua/scripts/seatSlots.lua")) {
            if(is == null) throw new RuntimeException("lua script not found");
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException e){
            throw new RuntimeException("lua script 로드 실패");
        }
    }

    @Transactional
    public SeatSlotDto reserveSlot(Long slotId, int count) {
        if (count < 1 || count > 4) {
            throw new BusinessException(ErrorCode.INVALID_SEAT_COUNT);
        }
        // DB 조회
        SeatSlots slot = repository.findById(slotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEATS_NOT_FOUND));

        String redisKey = "seatSlots:" + slot.getId();
        String luaScript = loadLuaScript();

        // Redisson 분산락(lock key: seatLock:{slotId})
        String lockKey = "seatLock:" + slot.getId();
        RLock lock = redissonClient.getLock(lockKey); //RLock: redisson에서 제공하는 분산락 객체
        try {
            // 최대 5초 대기 후 락 획득, 락 유지 시간 3초
            if(lock.tryLock(5, 3, TimeUnit.SECONDS)){
                // Lua 스크립트 실행
                Number result = redissonClient.getScript().eval( //getScript(): redis lua 스크립트 실행 객체 가져옴
                        RScript.Mode.READ_WRITE, // 스크립트 실행모드 읽기와 쓰기 모두 허용
                        luaScript,
                        RScript.ReturnType.INTEGER, // lua 스크립트 반환 타입
                        List.of(redisKey),  // KEYS[1] = "seatSlots:{slotId}"
                        count               // ARGV[1] = count (예약 좌석 수)
                );

                if(result.intValue() == -1){
                    throw new BusinessException(ErrorCode.SEATS_NOT_FOUND);
                } else if (result.intValue() == 0) {
                    throw new BusinessException(ErrorCode.INSUFFICIENT_SEATS);
                }
                // DB 업데이트
                slot.setAvailableSeats(slot.getAvailableSeats() - count);
                repository.save(slot);

                // DB 기준으로 Redis 동기화(장애 대비) = Redis 값을 DB 기준으로 강제로 맞춤
                redissonClient.getBucket(redisKey).set(slot.getAvailableSeats());

                SeatSlotDto dto = new SeatSlotDto();
                dto.setId(slot.getId());
                dto.setDate(slot.getDate());
                dto.setStartTime(slot.getStartTime());
                dto.setEndTime(slot.getEndTime());
                dto.setAvailableSeats(slot.getAvailableSeats());
                return dto;
            } else {
                throw new BusinessException(ErrorCode.LOCK_ACQUIRE_FAIL);
            }
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생");
        }finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    @Transactional
    public List<SeatSlotDto> getSlots(Long restaurantId, Long operatingDayId) {
        List<SeatSlots> slots = repository.findByOperatingDay_IdAndOperatingDay_Restaurant_Id(operatingDayId, restaurantId);

        return slots.stream()
                .map(slot -> {
                    SeatSlotDto dto = new SeatSlotDto();
                    dto.setId(slot.getId());
                    dto.setDate(slot.getDate());
                    dto.setStartTime(slot.getStartTime());
                    dto.setEndTime(slot.getEndTime());
                    dto.setAvailableSeats(slot.getAvailableSeats());
                    return dto;
                }).collect(Collectors.toList());
    }
}
