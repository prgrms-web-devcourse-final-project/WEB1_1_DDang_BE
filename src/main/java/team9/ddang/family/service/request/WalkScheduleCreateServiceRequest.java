package team9.ddang.family.service.request;

import team9.ddang.family.entity.DayOfWeek;

import java.time.LocalTime;

public record WalkScheduleCreateServiceRequest(
        Long memberId,
        LocalTime walkTime,
        DayOfWeek dayOfWeek,
        Long dogId
) {
}
