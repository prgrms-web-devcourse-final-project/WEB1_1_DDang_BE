package team9.ddang.family.service.request;

import team9.ddang.family.entity.DayOfWeek;

import java.time.LocalTime;
import java.util.List;

public record WalkScheduleCreateServiceRequest(
        Long memberId,
        LocalTime walkTime,
        List<DayOfWeek> dayOfWeek,
        Long dogId
) {
}
