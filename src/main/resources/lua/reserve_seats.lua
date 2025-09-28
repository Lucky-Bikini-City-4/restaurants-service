-- KEYS[1] : seat slot key, ex) seatSlots:123
-- ARGV[1] : 예약하려는 좌석 수

local availableSeats = tonumber(redis.call('GET', KEYS[1]))
local requestedSeats = tonumber(ARGV[1])

if availableSeats == nil then
    return -1  -- slot 없음
end

if availableSeats >= requestedSeats then
    redis.call('DECRBY', KEYS[1], requestedSeats)
    return 1  -- 예약 성공
else
    return 0  -- 좌석 부족
end
