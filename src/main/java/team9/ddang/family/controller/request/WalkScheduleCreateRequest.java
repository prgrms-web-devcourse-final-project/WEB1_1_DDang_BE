package team9.ddang.family.controller.request;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import team9.ddang.family.entity.DayOfWeek;
import team9.ddang.family.service.request.WalkScheduleCreateServiceRequest;

public record WalkScheduleCreateRequest(
        @NotNull(message = "산책 담당 맴버를 입력해주세요")
        Long memberId,
        @NotNull(message = "산책 시간을 입력해주세요.")
        LocalTime walkTime,
        @NotNull(message = "요일을 입력해주세요.")
        DayOfWeek dayOfWeek,
        @NotNull(message = "강아지 ID를 입력해주세요.")
        Long dogId
) {
    public WalkScheduleCreateServiceRequest toServiceRequest() {
        return new WalkScheduleCreateServiceRequest(memberId, walkTime, dayOfWeek, dogId);
    }
}
