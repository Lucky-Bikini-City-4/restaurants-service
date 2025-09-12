package com.dayaeyak.restaurants.seats.service;

import com.dayaeyak.restaurants.common.exception.BusinessException;
import com.dayaeyak.restaurants.common.exception.ErrorCode;
import com.dayaeyak.restaurants.seats.dto.SeatAvailabilityDto;
import com.dayaeyak.restaurants.seats.entity.SeatRestoreTask;
import com.dayaeyak.restaurants.seats.entity.Seats;
import com.dayaeyak.restaurants.seats.repository.SeatsRepository;
import com.dayaeyak.restaurants.seats.repository.SeatsRestoreTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SeatsService {

    private final SeatsRepository seatsRepository;
    private final SeatsRestoreTaskRepository taskRepository;

    @Transactional
    public SeatAvailabilityDto reserveSeats(Long restaurantId, LocalDate date, int count) {
        Seats seats = seatsRepository.findByRestaurantIdAndDate(restaurantId, date)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.SEATS_NOT_FOUND));

        if (seats.getAvailableSeats() < count) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_SEATS);
        }

        // 좌석 차감
        seats.setAvailableSeats(seats.getAvailableSeats() - count);
        seatsRepository.save(seats);

        // 좌석 복구 예약 기록 생성
        SeatRestoreTask task = new SeatRestoreTask();
        task.setSeatId(seats.getId());
        task.setRestoreCount(count);  // 차감된 좌석 수 저장
        task.setExecuteAt(LocalDateTime.now().plusMinutes(90));  // 실행된 시간으로부터 90분 뒤
        taskRepository.save(task);    // <- 태스크 저장 후 스케줄러에 의해 참조됨

        SeatAvailabilityDto dto = new SeatAvailabilityDto();
        dto.setDate(seats.getDate());
        dto.setAvailableSeats(seats.getAvailableSeats());
        return dto;
    }

    // 매 분마다 실행: 예약된 자석 복구 처리 , 스프링 스케줄러가 알아서 실행
    @Scheduled(cron = "0 * * * * ?") // 매분 마다 복구 예정
    @Transactional
    public void RestoreSeatsScheduled(){
        LocalDateTime now = LocalDateTime.now();       // ExecuteAtBefore: 실행 예정 시간이 지금보다 이전
        List<SeatRestoreTask> tasks = taskRepository.findByExecutedFalseAndExecuteAtBefore(now);

        for(SeatRestoreTask task : tasks){
            Seats seat = seatsRepository.findById(task.getSeatId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.SEATS_NOT_FOUND));

            seat.setAvailableSeats(seat.getAvailableSeats() + task.getRestoreCount());
            seatsRepository.save(seat);

            task.setExecuted(true);
            taskRepository.save(task);
        }
    }


    @Transactional(readOnly = true)
    public SeatAvailabilityDto getSeats(Long restaurantId, LocalDate date) {
        Seats seat = seatsRepository.findByRestaurantIdAndDate(restaurantId, date)
                .stream()
                .findFirst()
                .orElseThrow(()-> new BusinessException(ErrorCode.SEATS_NOT_FOUND));
        SeatAvailabilityDto dto = new SeatAvailabilityDto();
        dto.setDate(seat.getDate());
        dto.setAvailableSeats(seat.getAvailableSeats());
        return dto;
    }

}
