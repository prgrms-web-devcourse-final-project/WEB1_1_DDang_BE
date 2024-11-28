package team9.ddang.family.controller.request;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import team9.ddang.family.entity.DayOfWeek;
import team9.ddang.family.service.request.WalkScheduleCreateServiceRequest;

@Schema(description = "산책 일정 생성 데이터")
public record WalkScheduleCreateRequest(
        @Schema(description = "산책 담당 멤버 ID", example = "1")
        @NotNull(message = "산책 담당 멤버를 입력해주세요.")
        Long memberId,

        @Schema(description = "산책 시간 (HH:mm)", example = "09:30")
        @NotNull(message = "산책 시간을 입력해주세요.")
        LocalTime walkTime,

        @Schema(description = "요일", example = "MON", allowableValues = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"})
        @NotNull(message = "요일을 입력해주세요.")
        DayOfWeek dayOfWeek,

        @Schema(description = "강아지 ID", example = "5")
        @NotNull(message = "강아지 ID를 입력해주세요.")
        Long dogId
) {
    public WalkScheduleCreateServiceRequest toServiceRequest() {
        return new WalkScheduleCreateServiceRequest(memberId, walkTime, dayOfWeek, dogId);
    }
}
