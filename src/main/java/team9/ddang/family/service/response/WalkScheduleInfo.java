package team9.ddang.family.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.family.entity.DayOfWeek;

import java.time.LocalTime;

public record WalkScheduleInfo(
        @Schema(description = "산책 일정 ID", example = "1")
        Long walkScheduleId,

        @Schema(description = "산책 요일", example = "MONDAY")
        DayOfWeek dayOfWeek,

        @Schema(description = "산책 시간", example = "10:00")
        LocalTime walkTime
) {
}
